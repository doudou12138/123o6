package org.fffd.l23o6;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.service.impl.TrainServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainServiceTest {

//    @Mock
//    private TrainDao trainDao;
//
//    @Mock
//    private RouteDao routeDao;
//
//    @InjectMocks
//    private TrainServiceImpl trainService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void changeTrain_shouldUpdateTrainEntity() {
//        // Arrange
//        Long id = 1L;
//        String name = "New Train";
//        Long routeId = 2L;
//        TrainType type = TrainType.HIGH_SPEED;
//        String date = "2023-07-01";
//        List<Date> arrivalTimes = new ArrayList<>();
//        List<Date> departureTimes = new ArrayList<>();
//
//        TrainEntity trainEntity = new TrainEntity();
//        trainEntity.setId(id);
//        trainEntity.setName("Old Train");
//
//        RouteEntity routeEntity = new RouteEntity();
//        routeEntity.setId(routeId);
//        routeEntity.setStationIds(new ArrayList<>());
//
//        // Act
//        trainService.changeTrain(id, name, routeId, type, date, arrivalTimes, departureTimes);
//
//        // Assert
//        assertEquals(name, trainEntity.getName());
//        assertEquals(routeId, trainEntity.getRouteId());
//        assertEquals(type, trainEntity.getTrainType());
//        assertEquals(date, trainEntity.getDate());
//        assertEquals(arrivalTimes, trainEntity.getArrivalTimes());
//        assertEquals(departureTimes, trainEntity.getDepartureTimes());
//        // Verify that trainDao.save() was called once
//        verify(trainDao, times(1)).save(trainEntity);
//    }
//
//    @Test
//    void changeTrain_withInvalidRoute_shouldThrowException() {
//        // Arrange
//        Long id = 1L;
//        Long routeId = 2L;
//        TrainType type = TrainType.HIGH_SPEED;
//
//        TrainEntity trainEntity = new TrainEntity();
//        trainEntity.setId(id);
//
//        RouteEntity routeEntity = new RouteEntity();
//        routeEntity.setId(routeId);
//        routeEntity.setStationIds(new ArrayList<>());
//
//        // Act and Assert
//        assertThrows(null, () -> trainService.changeTrain(id, "New Train", routeId, type,
//                "2023-07-01", new ArrayList<>(), new ArrayList<>()));
//
//        // Verify that trainDao.save() was not called
//        verify(trainDao, never()).save(any());
//    }
}
