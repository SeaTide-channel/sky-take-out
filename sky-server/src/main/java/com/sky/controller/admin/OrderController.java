package com.sky.controller.admin;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("AdminOrderController")
@RequestMapping("admin/order")
@Slf4j
@Api(tags = "管理员订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //用户取消订单
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody Orders orders){
        //封装数据

        log.info("取消订单，订单id：{}",orders);

        orderService.cancelOrder(orders);
        return Result.success();
    }

    //订单列表
    @GetMapping("/conditionSearch")
    @ApiOperation("订单列表")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("分页查询订单：{}",ordersPageQueryDTO);
        return Result.success(orderService.pageQuery(ordersPageQueryDTO));
    }



    //查询订单详情
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> list(@PathVariable Long id) {
        log.info("查询订单详情，订单id：{}",id);
        return Result.success(orderService.details(id));
    }

}
