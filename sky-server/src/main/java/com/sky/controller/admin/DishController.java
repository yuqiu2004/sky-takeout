package com.sky.controller.admin;

import com.sky.constant.KeyConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController("adminDishController")
@Slf4j
@Api("菜品管理接口")
@RequestMapping("/admin/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result add(@RequestBody DishDTO dishDTO){
        dishService.addWithFlavor(dishDTO);
        clearCache(KeyConstant.REDIS_PREFIX_SHOP+dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分类查询
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品查询")
    public Result query(DishPageQueryDTO pageQueryDTO){
        PageResult pageResult = dishService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除菜品
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        clearCache(KeyConstant.REDIS_PREFIX_SHOP+"*");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result info(@PathVariable Long id){
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateWithFlavor(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        clearCache(KeyConstant.REDIS_PREFIX_SHOP+'*');
        return Result.success();
    }

    /**
     * 菜品状态修改
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品状态修改")
    public Result status(@RequestParam Long id, @PathVariable Integer status){
        dishService.updateDishStatusById(status, id);
        clearCache(KeyConstant.REDIS_PREFIX_SHOP+'*');
        return Result.success();
    }

    /**
     * 根据分类查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类查询菜品")
    public Result list(@RequestParam Long categoryId){

        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(KeyConstant.REDIS_PREFIX_SHOP + categoryId);
        if(dishVOList!=null && !dishVOList.isEmpty()) return Result.success(dishVOList);
        List<DishVO> list = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(KeyConstant.REDIS_PREFIX_SHOP+categoryId, list);
        return Result.success(list);
    }

    /**
     * 清除缓存
     * @param pattern
     */
    @ApiOperation("清除缓存")
    private void clearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
