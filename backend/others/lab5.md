#Lab5文档
|版本| 更新时间      |更新人| 当前版本 |
|---|-----------|---|------|
|v1.1| 2023-4.27 |汪天佑||
|v2.1| 2023-7.6  |汪天佑| yes  |

## 小组成员：
|姓名|学号|联系方式|
|---|---|---|
|汪天佑|211250173|18156945938|
|曾玉琨|211250160||
|金普凡|201250221||

小组会议时确定1.引言 2.概述描述，并就其他问题进行讨论；
会议后汪天佑整理完成逻辑视角、物理视角、开发视角内容，  
曾玉琨整理完成模块视角内容  
金普凡同学仍未联系到

# 1 引言
## 1.1 编制目的
本报告详细完成对TicketCube售票平台的概要设计，达到指导详细设计和开发的⽬的，同
时实现和测试⼈员以及⽤户的沟通。
本报告⾯向开发⼈员、测试⼈员及最终⽤户⽽编写，是了解系统的导航。
## 1.2 词汇表
|词汇名称|词汇含义|备注|
| --- | --- | --- |

## 1.3 参考文献
    1. IEEE标准
    2.《软件⼯程与计算（卷⼆）软件开发的技术基础》

# 2 产品概述
参考TicketCube系统⽤例⽂档和软件需求规格说明中对产品的概括描述
# 3 逻辑视图
- 处理静态设计模型

TicketCube售票平台系统中，选择了分层体系结构⻛格，将系统分为4部分
（presentation, controller, service, data）能够很好地示意整个⾼层抽象。Presentation部分包含
了GUI⻚⾯的实现，Controller部分负责接受前端发送的请求并分发给相应的Service，service部分
负责业务逻辑的实现，data部分负责数据的持久化和访问。分层体系结构的逻辑视⻆和逻辑设计⽅
案如下图所示。
![第一阶段逻辑视图](https://github.com/doudou12138/img/blob/86afed2afcf9bc1033c398e2728c8d7d215a9ae6/logicVersion.png?raw=true)
# 4 物理视角
![第一阶段物理视图](https://github.com/doudou12138/img/blob/86afed2afcf9bc1033c398e2728c8d7d215a9ae6/physicalVersion1.png?raw=true)
![第一阶段物理视图](https://github.com/doudou12138/img/blob/86afed2afcf9bc1033c398e2728c8d7d215a9ae6/physicalVersion2png.png?raw=true)
![第一阶段物理视图](https://github.com/doudou12138/img/blob/86afed2afcf9bc1033c398e2728c8d7d215a9ae6/physicalVersion3.png?raw=true)

# 5 开发视角
![第一阶段开发视图](https://github.com/doudou12138/img/blob/86afed2afcf9bc1033c398e2728c8d7d215a9ae6/programVersion.png?raw=true)

# 6 模块视图
## 6.1 客户端模块视图
客户端各层职责：
|层|职责|
|---|---|
|启动模块|负责初始化⽹络通信机制，启动⽤户界⾯|
|⽤户界⾯层|TicketCube客户端⽤户界⾯，使⽤Vue.js框架实现|
|客户端⽹络模块|实现前后端通信|
![客户端模块划分](https://github.com/doudou12138/img/blob/1f5b6e6e7eb91c69f6c3a0e7e762c7f5f889f577/userModuleView.jpg?raw=true)
![服务端网络划分](https://github.com/doudou12138/img/blob/1f5b6e6e7eb91c69f6c3a0e7e762c7f5f889f577/serviceModuleView.jpg?raw=true)
## 6.2 ⽤户界⾯层分解
根据第一阶段需求，共有八个页面，
其页面跳转情况如下：
![页面跳转](https://github.com/doudou12138/img/blob/1f5b6e6e7eb91c69f6c3a0e7e762c7f5f889f577/pageSwitch.png?raw=true)
### 6.2.1 职责
- 类图
用户端和客户端的用户界面接口是一样的。

|模块|功能|
| --- | --- |
|TicketControl|负责车票信息的管理|
|SelectControl|负责查询页面的功能|
|CustomerControl|负责用户界面的功能|
|TicketOrderControl|负责订单界面的管理|
|SaleControl|负责购买车票页面的功能|
|ReserveControl|负责用于更新库存|
### 6.2.2 接口规范
- Reservebl模块的接口规范：

|接口名|语法|前置条件|后置条件|
|---|---|---|---|
|ReserveService.getReserve|getReserve(Integer id)|id合法|返回该编号车票的库存|
|ReserveService.addReserve|addReserve(Integer id)|id合法|将该编号的车票的库存加一|
|ReserveService.subReserve|subReserve(Integer id)|id合法|将该编号的车票的库存减一|
|ReserveService.setReserve|setReserve(Integer id,Integer number)|id合法，number为正|将该编号的车票的库存更新为指定数量|

- Salebl模块的接口规范：

|接口名|语法|前置条件|后置条件|
|---|---|---|---|
|SaleService.setNumber|setNumber(int number)|number为正||
|SaleService.verify|verify()|Sale的车票号和数量合法|形成订单|
|SaleService.setTickID|setTickID(Integer id)|id合法||
## 6.3 数据层分解
### 6.3.1 数据层模块职责如下：
|模块|职责|
|---|---|
|TicketMapper|持久化数据库的接⼝。提供车票信息的集体载⼊、集体保存、增、删、改、查服务。|
|ReverseMapper|持久化数据库的接⼝。提供车票库存信息的
|OrderMapper|持久化数据库的接⼝。提供订单的集体载⼊、集体保存、增、删、改、查服务。|
|UserMapper|持久化数据库的接⼝。提供顾客信息的集体载⼊、集体保存、增、删、改、查服务。|
### 6.3.2 接口规范
|接口名|语法|前置条件|后置条件|
|---|---|---|---|
|ReverseMapper.selectByStatId|selectByStatId(Integer fromId,Integer arrId)|前后站id合法|返回符合条件的车票集合|
|ReverseMapper.selectById|selectById(Integer ticId)|tikid合法|返回符合条件的车票|
|ReverseMapper.setTicNumById|setTicNumById(Integer id,Integer number)|ticid合法|设置该编号车票数量|
|ReverseMapper.TicSelled|setTicNumById(Integer id)|ticid合法|设置该编号车票数-1|
|OrderMapper.addOrder|addOrder(Order order)|order不为空|形成新订单|
# 信息视角
描述数据持久化对象(PO)：系统的PO类就是对应的相关的实体类
- Ticket_PO

|属性|含义|类型|
|---|---|---|
|from_id|出发站的编号|Integer|
|arr_id|重点站的编号|Integer|
|price|车票单价|Integer|

- Reverse_PO

|属性|含义|类型|
|---|---|---|
|ticket|车票|Ticket|
|num|车票数量|Integer|

- Order_PO

|属性|含义|类型|
|---|---|---|
|tickets|车票|Tickets|
|user|购票用户|Customer|

-Customer_PO
|属性|含义|类型|
|---|---|---|
|name|名字|String|
|id|账号|String|
|password|密码|String|
