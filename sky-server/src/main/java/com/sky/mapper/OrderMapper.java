package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Mapper
public interface OrderMapper {


    //插入数据
    void insert(Orders order);


    //根据订单号查询订单
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    //根据id查询订单内菜品与备注之类的信息
    @Select("select * from orders where id = #{id}")
    OrderVO getDetailsById(Long id);

    //根据id查询订单的下单信息
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    //根据数字status查询各个状态的订单数量
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    //根据订单状态和下单时间查询订单信息
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    //查询营业额
    Double sumByDate(Map map);

    //查询订单的数量
    @Select("select count(*) from orders")
    Integer count();

    //根据日期查询订单数量
    Integer countByDate(Map map);

    //获取销量前10商品的表（包含名称和销量）
    List<GoodsSalesDTO> getTop10(LocalDateTime begin, LocalDateTime end);

}
