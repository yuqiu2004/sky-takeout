package com.sky.controller.user;

import com.sky.constant.KeyConstant;
import com.sky.entity.SetMeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("userSetMealController")
@Slf4j
@RequestMapping("/user/setmeal")
@Api("套餐管理")
public class SetMealController {

    @Resource
    private SetMealService setMealService;

    /**
     * 根据分类查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类查询套餐")
    @Cacheable(cacheNames = KeyConstant.REDIS_PREFIX_SET_MEAL, key = "#categoryId")
    public Result list(@RequestParam Long categoryId){
        List<SetMeal> list = setMealService.list(categoryId);
        return Result.success(list);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据id查询套餐菜品")
    public Result dish(@PathVariable String id){
        List<DishItemVO> list = setMealService.dish(id);
        return Result.success(list);
    }
}
