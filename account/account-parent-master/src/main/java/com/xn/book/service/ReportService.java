package com.xn.book.service;

import com.xn.book.request.ReportMoneyVo;
import com.xn.book.response.ReportMoneyRes;

import java.util.List;

/**
 * @author xn
 * @date 2023/1/31 11:30
 */
public interface ReportService {
    List<ReportMoneyRes> list(ReportMoneyVo reportMoneyVo) ;
}
