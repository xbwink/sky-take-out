package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-17 15:08
 * @vesion 1.0
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "管理端数据统计接口")
@Slf4j
public class ReportController {

    @Autowired
    ReportService reportService;

    @ApiOperation("营业额统计")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        TurnoverReportVO vo = reportService.turnoverStatistics(begin, end);
        return Result.success(vo);
    }

    @ApiOperation("用户统计")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        UserReportVO vo = reportService.userStatistics(begin, end);
        return Result.success(vo);
    }

    @ApiOperation("订单统计")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        OrderReportVO vo = reportService.ordersStatistics(begin, end);
        return Result.success(vo);
    }

    @ApiOperation("销量排名Top10")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        SalesTop10ReportVO vo = reportService.top10(begin, end);
        return Result.success(vo);
    }

    @ApiOperation("销量排名Top10")
    @GetMapping("/export")
    public Result export(HttpServletResponse response) {
        reportService.export(response);
        return Result.success();
    }


}
