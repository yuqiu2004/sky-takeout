package com.sky.controller.user;

import com.sky.constant.KeyConstant;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("userDishController")
@Slf4j
@Api("菜品管理接口")
@RequestMapping("/user/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("根据分类id获取菜品")
    @Cacheable(cacheNames = KeyConstant.REDIS_PREFIX_DISH, key = "#categoryId") // generate key: DISH::id
    public Result list(@RequestParam Long categoryId){
        List<DishVO> list = dishService.listWithFlavor(categoryId);
        return Result.success(list);
    }

}
