package com.xn.book.controller;

import com.xn.book.common.response.BaseResult;
import com.xn.book.common.response.ResultUtils;
import com.xn.book.request.ReportMoneyVo;
import com.xn.book.response.ReportMoneyRes;
import com.xn.book.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图表统计
 * @author xn
 * @date 2023/1/31 11:15
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    ReportService reportService;
    /**
     * 图表统计返回
     *
     *
     * @return
     */
    @PostMapping("/list")
    public BaseResult list(@RequestBody ReportMoneyVo reportMoneyVo) {
        List<ReportMoneyRes> reportCategoryRes=reportService.list(reportMoneyVo);
        return ResultUtils.success(reportCategoryRes);
    }


}
