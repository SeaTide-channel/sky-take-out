package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理订单数据异常
            //检测地址是否为空
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null)throw new RuntimeException(MessageConstant.ADDRESS_BOOK_IS_NULL);
            //检测该用户的购物车是否为空
        Long userId = BaseContext.getCurrentId();
        ShoppingCart queryCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(queryCart);
        if(shoppingCartList == null && shoppingCartList.size() == 0)throw new RuntimeException(MessageConstant.SHOPPING_CART_IS_NULL);

        //插入订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        orderMapper.insert(orders);

        //循环插入订单明细数据
            //创建一个存放OrderDetail的集合
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        for(ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);

        //清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        //返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

        //获取订单id
        String orderId = ordersPaymentDTO.getOrderNumber();

        //根据订单id查询订单信息
        Orders orderDB = orderMapper.getByNumber(orderId);

        //判断是否有查询到订单
        if(orderDB == null)throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        if(Objects.equals(orderDB.getPayStatus(), Orders.PAID))throw new OrderBusinessException(MessageConstant.ORDER_PAID);

        //订单支付状态修改
        Orders orders = Orders.builder()
                .id(orderDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payMethod(ordersPaymentDTO.getPayMethod())
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);


        return new OrderPaymentVO();
    }


    //订单分页查询
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        //给列表中每个套餐添加地址
        List<Orders> ordersList = page.getResult();
        for(Orders order : ordersList){
            AddressBook addressBook = addressBookMapper.getById(order.getAddressBookId());
            order.setAddress(addressBook.getProvinceName()
                    +addressBook.getCityName()
                    +addressBook.getDistrictName()
                    +addressBook.getDetail());

        }


        return new PageResult(page.getTotal(), ordersList);
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED) // 订单状态变为待派送
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    //根据id查询订单信息和订单明细
    public OrderVO details(Long id) {
        OrderVO orderVO = orderMapper.getDetailsById(id);
        orderVO.setOrderDetailList(orderDetailMapper.listById(id));

        //封装订单信息
            //根据套餐id获取套餐信息
        Orders orders = orderMapper.getById(id);
        String provinceName = addressBookMapper.getById(orders.getAddressBookId()).getProvinceName();
        String cityName = addressBookMapper.getById(orders.getAddressBookId()).getCityName();
        String districtName = addressBookMapper.getById(orders.getAddressBookId()).getDistrictName();
        String detail = addressBookMapper.getById(orders.getAddressBookId()).getDetail();

        orderVO.setAddress(provinceName + cityName + districtName + detail);
        orderVO.setUserName(userMapper.getById(orders.getUserId()).getName());
        orderVO.setPhone(userMapper.getById(orders.getUserId()).getPhone());
        orderVO.setOrderTime(orders.getOrderTime());
        orderVO.setPhone(orders.getPhone());

        return orderVO;
    }


    public void cancelOrder(Orders orders) {
        orderMapper.update(orders);
    }

}
