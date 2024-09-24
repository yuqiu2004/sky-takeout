package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.MinioUtil;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private SetMealDishMapper setMealDishMapper;

    @Resource
    private MinioUtil minioUtil;

    @Override
    public void addWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(null!=flavors && !flavors.isEmpty()){
            flavors.forEach( f -> f.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        //public class Page<E> extends ArrayList<E> implements Closeable 难怪这玩意可以接收结果
        Page<DishVO> page = dishMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getPageSize(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除---是否存在起售中的菜品？？
        List<Dish> list = dishMapper.getByIds(ids);
        for (Dish dish : list) {
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除---是否被套餐关联了？？
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);
        if (setMealIds != null && setMealIds.size() > 0) {
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }
        for (Dish dish : list) {
            minioUtil.removeByPreUrl(dish.getImage());
        }
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> list = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(list);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // 修改dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        // 修改口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach( f->f.setDishId(dish.getId()) );
        if(flavors != null && !flavors.isEmpty()){
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public void updateDishStatusById(Integer status, Long id) {
        dishMapper.updateStatusById(status, id);
    }

    @Override
    public List<DishVO> list(Long categoryId) {
        List<DishVO> list = dishMapper.list(categoryId);
        return list;
    }

    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        Dish dish = Dish.builder().categoryId(categoryId).status(StatusConstant.ENABLE).build();
        List<Dish> list = dishMapper.list(dish);
        ArrayList<DishVO> res = new ArrayList<>();
        for (Dish d : list) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            res.add(dishVO);
        }
        return res;
    }
}
