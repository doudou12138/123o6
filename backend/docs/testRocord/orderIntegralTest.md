### auther:汪天佑 date:2023-7-7
## 1.测试checkIntegral（采用黑盒测试，边界值测试）  
测试代码：
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
出现错误，积分机制有错误，对orderServiceImpl中的积分相关List进行修改 

修改测试代码：
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
测试正确