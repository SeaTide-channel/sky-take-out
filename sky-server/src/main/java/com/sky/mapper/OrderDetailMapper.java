package com.sky.mapper;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface OrderDetailMapper {

    //批量插入订单内的商品
    void insertBatch(List<OrderDetail> orderDetailList);

    //根据订单id查询订单内的商品
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> listById(Long id);

}
