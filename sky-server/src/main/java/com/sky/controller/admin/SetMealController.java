package com.sky.controller.admin;

import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/setmeal")
public class SetMealController {

    @Resource
    private SetMealService setMealService;

    /**
     * 分页查询套餐
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result query(SetMealPageQueryDTO dto){
        PageResult result = setMealService.pageQuery(dto);
        return Result.success(result);
    }

    /**
     * 新增套餐
     * @param setMealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result add(@RequestBody SetMealDTO setMealDTO){
        setMealService.add(setMealDTO);
        return Result.success();
    }
}
