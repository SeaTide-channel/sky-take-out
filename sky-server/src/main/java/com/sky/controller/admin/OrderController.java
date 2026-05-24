package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
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

    //接单
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单订单号：{}",ordersConfirmDTO);
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }


    //拒单
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("拒单：{}",ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    //店家取消订单
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单，订单id：{}",ordersCancelDTO.getId());

        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    //派送订单
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单，订单id：{}",id);

        orderService.delivery(id);
        return Result.success();
    }

    //完成订单
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        log.info("完成订单，订单id：{}",id);

        orderService.complete(id);
        return Result.success();
    }

    //查询各个状态的订单的数量
    @GetMapping("/statistics")
    @ApiOperation("查询各个状态的订单的数量")
    public Result<OrderStatisticsVO> statistics(){
        return Result.success(orderService.statistics());
    }
}
