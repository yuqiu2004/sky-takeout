package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("userCategoryController")
@Slf4j
@RequestMapping("/user/category")
@Api("用户端分类管理")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类列表
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取分类列表")
    public Result list(@RequestParam(required = false) Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }



}
