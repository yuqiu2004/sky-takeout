package com.sky.controller.admin;

import com.sky.constant.KeyConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api("管理端店铺管理")
public class ShopController {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 修改店铺状态
     * @param status
     * @return
     */
    @ApiOperation("修改店铺状态")
    @PutMapping("/{status}")
    public Result status(@PathVariable String status){
        redisTemplate.opsForValue().set(KeyConstant.SHOP_STATUS_KEY, status);
        // TODO: 这玩意配置怎么读取的？
        return Result.success();
    }

    /**
     * 查询店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺状态")
    public Result getStatus(){
        Object status = redisTemplate.opsForValue().get(KeyConstant.SHOP_STATUS_KEY);
        int parseInt = Integer.parseInt((String) status);
        return Result.success(parseInt);
    }
}
