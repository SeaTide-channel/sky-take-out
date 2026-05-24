package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    //用户下单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);


    //订单支付
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    //支付成功，修改订单状态
    void paySuccess(String outTradeNo);

    //分页查询订单
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    //接单
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    //取消订单
    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    //根据id查询订单信息和订单信息
    OrderVO details(Long id);

    //拒单
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    //派送订单
    void delivery(Long id);

    //完成订单
    void complete(Long id);

    //订单统计
    OrderStatisticsVO statistics();
}
