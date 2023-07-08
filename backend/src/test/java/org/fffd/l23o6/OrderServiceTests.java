package org.fffd.l23o6;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.OrderEntity;

import org.fffd.l23o6.pojo.entity.UserEntity;

import org.fffd.l23o6.service.impl.OrderServiceImpl;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;


import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.mockito.Mockito.when;

public class OrderServiceTests {

    @Mock
    private UserDao userDao;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderServiceImpl orderService;

    public OrderServiceTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCheckIntegral() {
//        List<Long> downBound = Arrays.asList(1000L, 3000L, 10000L, 50000L);
//        orderService.getDownBound(downBound);

        // 测试边界情况
        Assertions.assertEquals(0, orderService.checkIntegral(0));  // 应返回 0
        Assertions.assertEquals(0, orderService.checkIntegral(999));  // 应返回 0
        Assertions.assertEquals(1, orderService.checkIntegral(1000));  // 应返回 1
        Assertions.assertEquals(1, orderService.checkIntegral(2999));  // 应返回 1
        Assertions.assertEquals(2, orderService.checkIntegral(3000));  // 应返回 2
        Assertions.assertEquals(2, orderService.checkIntegral(9999));  // 应返回 2
        Assertions.assertEquals(3, orderService.checkIntegral(10000));  // 应返回 3
        Assertions.assertEquals(3, orderService.checkIntegral(49999));  // 应返回 3
        Assertions.assertEquals(4, orderService.checkIntegral(50000));  // 应返回 4
        Assertions.assertEquals(4, orderService.checkIntegral(50001));  // 应返回 -1
    }

    @Test
    public void testCalNewPrice() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(1L);
        when(orderDao.findById(1L)).thenReturn(Optional.of(orderEntity));

        UserEntity userEntity = new UserEntity();
        userEntity.setIntegral(2000L);
        when(userDao.findByid(1L)).thenReturn(userEntity);


        // 使用积分
        List<Double> expectedUseIntegral = Arrays.asList(0.0015, 2000.0);
        List<Double> actualUseIntegral = orderService.calNewPrice(1L, true);
        Assertions.assertEquals(expectedUseIntegral, actualUseIntegral);

        // 不使用积分
        List<Double> expectedNoIntegral = Arrays.asList(0.0, 0.0);
        List<Double> actualNoIntegral = orderService.calNewPrice(1L, false);
        Assertions.assertEquals(expectedNoIntegral, actualNoIntegral);
    }
}
