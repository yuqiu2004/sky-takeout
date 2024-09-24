package com.sky.controller.admin;

import com.sky.constant.KeyConstant;
import com.sky.dto.SetMealDTO;
import com.sky.dto.SetMealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetMealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("adminSetMealController")
@Api("套餐管理")
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
    @CacheEvict(cacheNames = KeyConstant.REDIS_PREFIX_SET_MEAL, key = "#setMealDTO.getCategoryId()")
    public Result add(@RequestBody SetMealDTO setMealDTO){
        setMealService.add(setMealDTO);
        return Result.success();
    }

    /**
     * 删除套餐
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = KeyConstant.REDIS_PREFIX_SET_MEAL, allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        setMealService.delete(ids);
        return Result.success();
    }

    /**
     * 修改套餐管理
     * @param setMealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = KeyConstant.REDIS_PREFIX_SET_MEAL, allEntries = true)
    public Result update(@RequestBody SetMealDTO setMealDTO){
        setMealService.update(setMealDTO);
        return Result.success();
    }

    /**
     * 套餐停售起售
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐停售起售")
    @CacheEvict(cacheNames = KeyConstant.REDIS_PREFIX_SET_MEAL, allEntries = true)
    public Result status(@PathVariable Integer status, @RequestParam Long id){
        setMealService.status(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取套餐")
    public Result getInfo(@PathVariable String id){
        SetMealVO setMealVO = setMealService.getById(id);
        return Result.success(setMealVO);
    }
}
