package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api("工作台管理")
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Resource
    private WorkspaceService workspaceService;

    /**
     * 今日数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("今日数据")
    public Result businessData(){
        return Result.success(workspaceService.businessData());
    }

    /**
     * 订单浏览
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("订单浏览")
    public Result overviewOrders(){
        return Result.success(workspaceService.overviewOrders());
    }

    /**
     * 菜品浏览
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("菜品浏览")
    public Result overviewDishes(){
        return Result.success(workspaceService.overviewDishes());
    }

    /**
     * 套餐浏览
     * @return
     */
    @GetMapping("/overviewSetMeals")
    @ApiOperation("套餐浏览")
    public Result overviewSetMeal(){
        return Result.success(workspaceService.overviewSetmeals());
    }

}
