package com.sky.controller.user;

import com.sky.constant.KeyConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("userShopController")
@Slf4j
@RequestMapping("/user/shop")
@Api("用户端店铺管理")
public class ShopController {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getStatus(){
        Object status = redisTemplate.opsForValue().get(KeyConstant.SHOP_STATUS_KEY);
        int parseInt = Integer.parseInt((String) status);
        return Result.success(parseInt);
    }
}
