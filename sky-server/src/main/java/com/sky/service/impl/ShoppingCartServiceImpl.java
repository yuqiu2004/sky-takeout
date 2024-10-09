package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetMeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.utils.MinioUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetMealMapper setMealMapper;

    @Resource
    private MinioUtil minioUtil;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, cart);
        cart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if(null != list && !list.isEmpty()){ // 增加数量
            cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.update(cart);
        }else{ // 直接插入
            // 填充冗余字段
            if(cart.getDishId() != null){ // 菜品
                Dish dish = dishMapper.getById(cart.getDishId());
                cart.setName(dish.getName());
                cart.setAmount(dish.getPrice());
                cart.setImage(dish.getImage());
            }else{
                SetMeal setMeal = setMealMapper.getById(cart.getSetMealId());
                cart.setImage(setMeal.getImage());
                cart.setName(setMeal.getName());
                cart.setAmount(setMeal.getPrice());
            }
            cart.setNumber(1);
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(cart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        list.forEach( l-> l.setImage(minioUtil.preview(l.getImage())));
        return list;
    }

    @Override
    public void clear() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 先查询 数量1直接删除 数量以上减一
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, cart);
        cart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if(null == list || list.isEmpty()) return;
        ShoppingCart shoppingCart = list.get(0);
        Integer number = shoppingCart.getNumber();
        if(number == 1){
            shoppingCartMapper.deleteById(shoppingCart.getId());
        }else{
            shoppingCart.setNumber(number-1);
            shoppingCartMapper.update(shoppingCart);
        }
    }
}
