package org.fffd.l23o6.controller;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.fffd.l23o6.pojo.vo.order.CreateOrderRequest;
import org.fffd.l23o6.pojo.vo.order.OrderIdVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.pojo.vo.order.PatchOrderRequest;
import org.fffd.l23o6.service.OrderService;
import org.springframework.web.bind.annotation.*;

import cn.dev33.satoken.stp.StpUtil;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("order")
    public CommonResponse<OrderIdVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        StpUtil.checkLogin();
        return CommonResponse.success(new OrderIdVO(orderService.createOrder(StpUtil.getLoginIdAsString(), request.getTrainId(), request.getStart_station_id(), request.getEnd_station_id(), request.getSeat_type(),(long)1)));
    }

    @GetMapping("order")
    public CommonResponse<List<OrderVO>> listOrders(){
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listOrders(StpUtil.getLoginIdAsString()));
    }

    @GetMapping("order/{orderId}")
    public CommonResponse<OrderVO> getOrder(@PathVariable("orderId") Long orderId) {
        return CommonResponse.success(orderService.getOrder(orderId));
    }

    @PatchMapping("order/{orderId}/{isChecked}/{payWay}")
    public CommonResponse<?> patchOrder(@PathVariable("orderId") Long orderId, @PathVariable("isChecked") boolean isChecked,@PathVariable("payWay") boolean payWay,@Valid @RequestBody PatchOrderRequest request) {

        boolean success = true;
        switch (request.getStatus()) {
            case PAID:
                success = orderService.payOrder(orderId,isChecked,payWay);
                break;
            case CANCELLED:
                success = orderService.cancelOrder(orderId);
                break;
            default:
                throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "Invalid order status.");
        }
        if(!success){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "pay fail");
        }

        return CommonResponse.success();
    }

    @GetMapping("order/calPrice")
    public CommonResponse<List<Double>> calPriceByDiscount(Long orderId,boolean isChecked){
        return CommonResponse.success(orderService.calNewPrice(orderId,isChecked));
    }
}