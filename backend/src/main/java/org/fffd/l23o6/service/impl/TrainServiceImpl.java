package org.fffd.l23o6.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainDetailVO getTrain(Long trainId) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfos()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date) {
        // TODO:finished
        // First, get all routes contains [startCity, endCity]
        List<RouteEntity> routeEntities = routeDao.findAll();
        List<Long> routeIds = new ArrayList<>();
        for(RouteEntity r:routeEntities){
            long ableArrive = r.ableArrive(startStationId,endStationId);
            if(ableArrive!=-1){
                routeIds.add(ableArrive);
            }
        }
        // Then, Get all trains on that day with the wanted routes
        List<TrainEntity> trainEntities = null;
        trainEntities = trainDao.findAllByDateAndRoutes(date,routeIds);

        List<TrainVO> trainVos = trainEntities.stream().map(TrainMapper.INSTANCE::toTrainVO).toList();

        for(int i=0;i<trainEntities.size();++i){
            TrainEntity t = trainEntities.get(i);
            RouteEntity route = routeDao.findById(t.getRouteId()).get();
            List<Long> stationIds = route.getStationIds();
            int startStationIndex = stationIds.indexOf(startStationId);
            int endStationIndex = stationIds.indexOf(endStationId);
            List<TicketInfo> ticketInfos = new ArrayList<>();
            switch (t.getTrainType()){
                case HIGH_SPEED:
                    GSeriesSeatStrategy gInstance = GSeriesSeatStrategy.INSTANCE;
                    Map<GSeriesSeatStrategy.GSeriesSeatType,Integer> map = gInstance.getLeftSeatCount(startStationIndex,endStationIndex,t.getSeats());

                    for(GSeriesSeatStrategy.GSeriesSeatType type:map.keySet()){
                        //todo:price
                        ticketInfos.add(new TicketInfo(type.getText(),map.get(type),gInstance.getPrice(type)*(endStationIndex-startStationIndex)));
                    };
                    break;
                case NORMAL_SPEED:
                    KSeriesSeatStrategy kInstance = KSeriesSeatStrategy.INSTANCE;
                    Map<KSeriesSeatStrategy.KSeriesSeatType,Integer> map1 = KSeriesSeatStrategy.INSTANCE.getLeftSeatCount(startStationIndex,endStationIndex,t.getSeats());
                    for(KSeriesSeatStrategy.KSeriesSeatType type:map1.keySet()){
                        ticketInfos.add(new TicketInfo(type.getText(),map1.get(type),kInstance.getPrice(type)*(endStationIndex-startStationIndex)));
                    }
                    break;
                default:
                    break;
            }

            trainVos.get(i).setStartStationId(startStationId);
            trainVos.get(i).setEndStationId(endStationId);
            trainVos.get(i).setDepartureTime(t.getDepartureTimes().get(startStationIndex));
            trainVos.get(i).setArrivalTime(t.getArrivalTimes().get(endStationIndex));
            trainVos.get(i).setTicketInfo(ticketInfos);
        }
        return trainVos;
    }

    @Override
    public List<AdminTrainVO> listTrainsAdmin() {
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes) {
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
                            List<Date> departureTimes) {
        // TODO:finished edit train info, please refer to `addTrain` above
        TrainEntity entity = trainDao.findById(id).get();
        entity.setName(name).setRouteId(routeId).setTrainType(type).setDate(date).setArrivalTimes(arrivalTimes)
                .setDepartureTimes(departureTimes);
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }

        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void deleteTrain(Long id) {
        trainDao.deleteById(id);
    }
}
