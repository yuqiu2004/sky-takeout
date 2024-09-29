package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api("报表数据管理")
public class ReportController {

    @Resource
    private ReportService reportService;

    /**
     * 营业额统计
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result turnover(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return Result.success(reportService.turnover(begin, end));
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result user(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return Result.success(reportService.user(begin, end));
    }

    /**
     * 订单统计
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result order(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return Result.success(reportService.order(begin, end));
    }
}
