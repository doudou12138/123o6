### auther:汪天佑 date:2023-7-7
# ⼀、测试背景
编写此报告的⽬的在于清楚地阐述12306项⽬中与测试相关的所有内容。包括背景、⼈员、平 台、内容、结果。
## 1.1 ⽬的
为了更好地辅助开发、保证产品的正确性安全性、提⾼⽤户信赖程度⽽进⾏明确的、有⽬的的、有效率
的测试。
## 1.2 参考⽂献
[1] 《软件⼯程与计算II——软件开发的技术基础》

# ⼆、测试平台
后端本地测试平台为：WINDOWS、JDK1.8、JUnit、MySQL数据库（单元测试、集成测试）。

# 后端测试
## 函数封装测试
### 1.测试checkIntegral（采用黑盒测试，边界值测试）  
#### 测试代码：
~~~
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
~~~
#### 测试结果：出现错误，积分机制有错误，
##### 错误处理：
积分机制的逻辑存在错误，对orderServiceImpl中的积分相关List进行修改
修改测试代码重新测试：
~~~
@Test
    public void testCheckIntegral() {
//        List<Long> downBound = Arrays.asList(1000L, 3000L, 10000L, 50000L);
//        orderService.getDownBound(downBound);

        // 测试边界情况
        Assertions.assertEquals(0, orderService.checkIntegral(0));  // 应返回 0
        Assertions.assertEquals(0, orderService.checkIntegral(999));  // 应返回 0
        Assertions.assertEquals(1, orderService.checkIntegral(1000));  // 应返回 1
        Assertions.assertEquals(1, orderService.checkIntegral(1999));  // 应返回 1
        Assertions.assertEquals(2, orderService.checkIntegral(2000));  // 应返回 2
        Assertions.assertEquals(2, orderService.checkIntegral(6999));  // 应返回 2
        Assertions.assertEquals(3, orderService.checkIntegral(7000));  // 应返回 3
        Assertions.assertEquals(3, orderService.checkIntegral(39999));  // 应返回 3
        Assertions.assertEquals(4, orderService.checkIntegral(40000));  // 应返回 4
        Assertions.assertEquals(4, orderService.checkIntegral(49999));  // 应返回 -1
        Assertions.assertEquals(5, orderService.checkIntegral(50000));  // 应返回 -1
        Assertions.assertEquals(5, orderService.checkIntegral(50001));  // 应返回 -1
    }
~~~
测试结果正确

### 测试calNewPrice
黑盒测试，代表值测试
#### 测试代码：
~~~
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
~~~
#### 测试结果：正确
