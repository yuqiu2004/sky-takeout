package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额数据
     * @return
     */
    TurnoverReportVO turnover(LocalDate begin, LocalDate end);
}
