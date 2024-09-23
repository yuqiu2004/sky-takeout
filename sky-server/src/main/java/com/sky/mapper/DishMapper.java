package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 插入菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * @param pageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO pageQueryDTO);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     * @param id
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id更新
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void updateById(Dish dish);

    /**
     * 修改菜品状态
     * @param id
     */
    @Update("update dish set status = !status where id = #{id}")
    @AutoFill(OperationType.UPDATE)
    void updateStatusById(Long id);

    @Select("select * from dish where category_id = #{categoryId}")
    List<DishVO> list(Long categoryId);

    /**
     * 根据ids批量查询
     * @param ids
     * @return
     */
    List<Dish> getByIds(List<Long> ids);
}
