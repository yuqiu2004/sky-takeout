package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.SetMeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetMealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    /**
     * 根据id获取套餐
     * @param ids
     * @return
     */
    List<SetMeal> selectByIds(List<Long> ids);

    /**
     * 根据id批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新修改套餐
     * @param setMeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(SetMeal setMeal);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     */
    @Update("update set_meal set status = #{status} where id = #{id}")
    void updateStatus(Integer status, Long id);

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @Select("select * from set_meal where id = #{id}")
    SetMeal getById(Long id);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Select("select * from set_meal where category_id = #{categoryId}")
    List<SetMeal> list(Long categoryId);

    /**
     * 根据套餐id获取菜品
     * @param id
     * @return
     */
    @Select("select d.name, sd.copies, d.image, d.description from set_meal_dish sd " +
            "left join dish d " +
            "on d.id = sd.dish_id " +
            "and sd.set_meal_id = #{id}")
    List<DishItemVO> getDishByCategoryId(String id);
}
