package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO turnover(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = orderMapper.getSumDuring(Orders.STATUS_COMPLETED, beginTime, endTime);
            if(turnover == null) turnover = 0.0;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .turnoverList(StringUtils.join(turnoverList, ','))
                .build();
    }

    @Override
    public UserReportVO user(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 找出创建时间在区间中的 以及在end前的所有
            newUserList.add(userMapper.countNewUser(beginTime, endTime));
            totalUserList.add(userMapper.countTotalUser(endTime));
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .newUserList(StringUtils.join(newUserList, ','))
                .totalUserList(StringUtils.join(totalUserList, ','))
                .build();
    }

    @Override
    public OrderReportVO order(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderList = new ArrayList<>();
        Integer totalOrder;
        Integer validOrder;
        Double rate = 0.0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            orderCountList.add(orderMapper.countOrder(beginTime, endTime, null));
            validOrderList.add(orderMapper.countOrder(beginTime, endTime, Orders.STATUS_COMPLETED));
        }
        totalOrder = orderCountList.stream().reduce(Integer::sum).get();
        validOrder = validOrderList.stream().reduce(Integer::sum).get();
        if(totalOrder != 0) rate = validOrder.doubleValue()/totalOrder.doubleValue();
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .orderCountList(StringUtils.join(orderCountList, ','))
                .validOrderCountList(StringUtils.join(validOrderList, ','))
                .totalOrderCount(totalOrder)
                .validOrderCount(validOrder)
                .orderCompletionRate(rate)
                .build();
    }
}
