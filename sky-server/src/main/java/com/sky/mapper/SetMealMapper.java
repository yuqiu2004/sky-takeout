package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.SetMeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetMealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealMapper {

    /**
     * 分页查询套餐
     * @param setMeal
     * @return
     */
    Page<SetMealVO> queryWithCategoryName(SetMeal setMeal);

    /**
     * 新增套餐
     * @param setMeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(SetMeal setMeal);
}
