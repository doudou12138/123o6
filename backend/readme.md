#操作
## 为用户添加积分和信誉分(<=100)
1. 修改userEntity
2. 修改register()
3. 修改userVo

与userName一样不可被用户编辑

## 在作为策略中加入了无座的map，不然会出错
在查询时要传给前端剩余座位数，这样才能买票
在策略中还加入了各类别的价格,提供了得到价格的方法简单地认为票价是类别*站数
还另外实现了releaseSeat方法，在cancel订单时需要release seat
为order添加费用price属性