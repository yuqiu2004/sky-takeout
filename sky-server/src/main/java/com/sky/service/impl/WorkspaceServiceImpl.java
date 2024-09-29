package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderMapper orderMapper;

    @Override
    public BusinessDataVO businessData() {
        // 获取当前的日期
        LocalDate today = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(today, LocalTime.MAX);
        // 查询当天的相关数据
        Integer newUser = userMapper.countNewUser(beginTime, endTime);
        if(newUser == null) newUser = 0;
        Integer countOrder = orderMapper.countOrder(beginTime, endTime, null);
        if(countOrder == null) countOrder = 0;
        Integer validOrder = orderMapper.countOrder(beginTime, endTime, Orders.STATUS_COMPLETED);
        if(validOrder == null) validOrder = 0;
        Double sum = orderMapper.getSumDuring(Orders.STATUS_COMPLETED, beginTime, endTime);
        if(sum == null) sum = 0.0;
        Double unitPrice = validOrder == 0 ? 0.0 : sum/validOrder.doubleValue();
        return BusinessDataVO.builder()
                .newUsers(newUser)
                .orderCompletionRate(validOrder.doubleValue()/countOrder.doubleValue())
                .turnover(sum)
                .validOrderCount(validOrder)
                .unitPrice(unitPrice)
                .build();
    }
}
