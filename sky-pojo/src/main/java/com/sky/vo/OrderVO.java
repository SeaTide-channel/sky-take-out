package com.sky.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends Orders implements Serializable {

    //订单菜品信息
    private String orderDishes;

    //地址
    private String address;

    //订单详情
    private List<OrderDetail> orderDetailList;

    //格式化时间
    @JsonFormat(pattern = "yyyy年MM月dd日HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime orderTime;
}
