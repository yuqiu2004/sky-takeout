package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetMealOverViewVO;

import java.time.LocalDate;

public interface WorkspaceService {

    /**
     * 今日数据
     * @return
     */
    BusinessDataVO businessData();

    /**
     * 范围数据
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO businessData(LocalDate begin, LocalDate end);

    /**
     * 订单浏览
     * @return
     */
    OrderOverViewVO overviewOrders();

    /**
     * 菜品浏览
     * @return
     */
    DishOverViewVO overviewDishes();

    /**
     * 套餐浏览
     * @return
     */
    SetMealOverViewVO overviewSetmeals();
}
