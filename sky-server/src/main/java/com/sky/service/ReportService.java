package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额数据
     * @return
     */
    TurnoverReportVO turnover(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO user(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO order(LocalDate begin, LocalDate end);

    /**
     * 销量前十
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

    /**
     * 导出数据
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
