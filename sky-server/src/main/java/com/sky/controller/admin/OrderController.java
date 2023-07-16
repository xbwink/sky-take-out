package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
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

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端订单接口")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> pageQueryHistoryOrders(OrdersPageQueryDTO dto) {
        PageResult pageResult = orderService.pageQueryOrders(dto);
        return Result.success(pageResult);
    }

    @ApiOperation("各个状态的订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/details/{orderId}")
    public Result<OrderVO> queryOrderDetail(@PathVariable Long orderId) {
        OrderVO orderVO = orderService.queryOrderDetail(orderId);
        return Result.success(orderVO);
    }

    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO dto) {
        orderService.confirm(dto.getId());
        return Result.success();
    }

    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO dto) {
        orderService.rejection(dto);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO dto) {
        orderService.cancel(dto);
        return Result.success();
    }

    @ApiOperation("派送订单")
    @PutMapping("/delivery/{orderId}")
    public Result delivery(@PathVariable Long orderId) {
        orderService.delivery(orderId);
        return Result.success();
    }

    @ApiOperation("完成订单")
    @PutMapping("/complete/{orderId}")
    public Result complete(@PathVariable Long orderId) {
        orderService.complete(orderId);
        return Result.success();
    }
}
