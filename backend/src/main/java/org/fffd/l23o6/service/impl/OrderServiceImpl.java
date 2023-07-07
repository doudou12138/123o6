package org.fffd.l23o6.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.payment.AliPayStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    private static final List<Integer> down_bound=new ArrayList<>(){{
        add(0);
        add(1000);
        add(3000);
        add(10000);
        add(50000);
    }};
    private static final List<Integer> consume = new ArrayList<>(){{
        add(0);
        add(1000);
        add(2000);
        add(7000);
        add(40000);
        add(50000);
    }};
    private static final List<Double> discounts=new ArrayList<>(){{
        add(0.0);
        add(0.001);
        add(0.0015);
        add(0.002);
        add(0.0025);
        add(0.003);
    }};


    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType,
            Long seatNumber) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        long price = 0;
        switch (train.getTrainType()) {
            case HIGH_SPEED:
                seat = GSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType), train.getSeats());
                price = (long) (endStationIndex - startStationIndex) *GSeriesSeatStrategy.INSTANCE.getPrice(GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType));
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType), train.getSeats());
                price = (long) (endStationIndex-startStationIndex)*KSeriesSeatStrategy.INSTANCE.getPrice(KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType));
                break;
            default:
                break;
        }
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId).price(price)
                .build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
    }

    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1,o2)-> o2.getId().compareTo(o1.getId()));
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();

            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .createdAt(order.getCreatedAt())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .price(order.getPrice())
                    .build();
        }).collect(Collectors.toList());
    }

    public OrderVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .price(order.getPrice())
                .build();
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // TODO: cancel is not return,not refund user's money and credits if needed
        //just release the seat
        TrainEntity trainEntity = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(trainEntity.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endStationIndex = route.getStationIds().indexOf(order.getArrivalStationId());

        TrainType trainType = trainEntity.getTrainType();
        if(trainType==TrainType.HIGH_SPEED){
            GSeriesSeatStrategy gSeriesSeatStrategy = GSeriesSeatStrategy.INSTANCE;
            System.err.println("startRelease");
            gSeriesSeatStrategy.releaseSeat(startStationIndex,endStationIndex,order.getSeat(),trainEntity.getSeats());
        }else if(trainType == TrainType.NORMAL_SPEED){
            KSeriesSeatStrategy kSeriesSeatStrategy = KSeriesSeatStrategy.INSTANCE;
            kSeriesSeatStrategy.releaseSeat(startStationIndex,endStationIndex,order.getSeat(),trainEntity.getSeats());
        }

        order.setStatus(OrderStatus.CANCELLED);
        trainEntity.setUpdatedAt(null);
        trainDao.save(trainEntity);
        orderDao.save(order);
    }

    public void payOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        int integralSeq = checkIntegral(userDao.findByid(order.getUserId()).getIntegral());
        // TODO: finished;use payment strategy to pay!
        boolean paied = AliPayStrategy.INSTANCE.pay(order.getPrice()*(1-discounts.get(integralSeq)));
        // TODO: finished;update user's integral, so that user can get discount next time
        if(paied){
            UserEntity userEntity = userDao.findByid(order.getUserId());
            userEntity.setIntegral(userEntity.getIntegral()-consume.get(integralSeq));
            userEntity.setIntegral(userEntity.getIntegral()+order.getPrice());
            order.setStatus(OrderStatus.COMPLETED);
            userEntity.setUpdatedAt(null);
            userDao.save(userEntity);
            orderDao.save(order);
        }

    }

    public int checkIntegral(long integral){
        for(int i=0;i<down_bound.size()-1;++i){
            if(integral>=down_bound.get(i)&&integral<down_bound.get(i+1)){
                return i;
            }
        }
        if(integral>=down_bound.get(down_bound.size()-1)){
            return down_bound.size()-1;
        }
        return -1;
    }

}
