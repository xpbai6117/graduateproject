package com.xn.book.scheduled;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xn.book.dto.WxMessageSendDTO;
import com.xn.book.entity.*;
import com.xn.book.entity.BookMoney;
import com.xn.book.entity.BookUser;
import com.xn.book.entity.User;
import com.xn.book.enums.BudgetStatusEnum;
import com.xn.book.enums.MessageTypeEnum;
import com.xn.book.service.*;
import com.xn.book.entity.BookBudgetInfo;
import com.xn.book.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class BookMessageTask {

    @Autowired
    private BookBudgetService bookBudgetService;

    @Autowired
    private BookMoneyService bookMoneyService;

    @Autowired
    private BookUserService bookUserService;

    @Autowired
    private WxService wxService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    private final String EARLY_WARNING_REMARK = "您的预算即将耗尽，请留意";

    private final String WARNING_REMARK = "您的预算已经超支，请留意";


    //@Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "0 0 5 * * ?")
    public void budgetMessage(){
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();

        List<BookBudgetInfo> list = bookBudgetService.list(new LambdaQueryWrapper<BookBudgetInfo>()
                .eq(BookBudgetInfo::getStatus, BudgetStatusEnum.ENABLE.getValue()));
        if (list.size() > 0) {
            list.forEach(c -> {
                Integer bookId = c.getBookId();
                Long budget = c.getBudget();

                List<BookMoney> bookMonies = bookMoneyService.list(new LambdaQueryWrapper<BookMoney>()
                        .eq(BookMoney::getBookId, bookId)
                        .eq(BookMoney::getType,1)
                        .between(BookMoney::getBookTime,getFirstDayOfMonth(new Date()),getLastDayOfMont(new Date())));

                Long reduce = bookMonies.stream().map(BookMoney::getMoney).reduce(0L, Long::sum);
                if (reduce <= budget && (reduce + 300) > budget){
                    BigDecimal amount = BigDecimal.valueOf(budget).subtract(BigDecimal.valueOf(reduce).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                    budgetEarlyWarning(c,amount);
                }

                if (reduce > budget){
                    BigDecimal amount = BigDecimal.valueOf(reduce).subtract(BigDecimal.valueOf(budget).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                    budgetWarning(c,amount);
                }
            });
        }
    }


    public   Date getFirstDayOfMonth(Date date){

        DateTime dateTime = DateUtil.beginOfMonth(date);
        Date date1 = dateTime.toJdkDate();
        return date1;
    }

    public  Date getLastDayOfMont(Date date){
        DateTime dateTime = DateUtil.endOfMonth(date);
        return dateTime.toJdkDate();
    }


    public void budgetEarlyWarning(BookBudgetInfo bookBudgetInfo,BigDecimal amount){
        List<BookUser> bookUsers = bookUserService.list(new LambdaQueryWrapper<BookUser>()
                .eq(BookUser::getBookId, bookBudgetInfo.getBookId()));

        //Book book = bookService.getById(bookBudgetInfo.getBookId());
        bookUsers.forEach(u -> {
            User user = userService.getById(u.getUserId());
            WxMessageSendDTO wxMessageSendDTO = new WxMessageSendDTO();
            wxMessageSendDTO.setOpenid(user.getOpenid());
            wxMessageSendDTO.setAmount(amount);
            wxMessageSendDTO.setRemark(EARLY_WARNING_REMARK);
            wxMessageSendDTO.setType(MessageTypeEnum.EARLY_WARNING.getValue());
            wxService.sendMessage(wxMessageSendDTO);
        });
    }

    public void budgetWarning(BookBudgetInfo bookBudgetInfo,BigDecimal amount){
        List<BookUser> bookUsers = bookUserService.list(new LambdaQueryWrapper<BookUser>()
                .eq(BookUser::getBookId, bookBudgetInfo.getBookId()));

        //Book book = bookService.getById(bookBudgetInfo.getBookId());
        bookUsers.forEach(u -> {
            User user = userService.getById(u.getUserId());
            WxMessageSendDTO wxMessageSendDTO = new WxMessageSendDTO();
            wxMessageSendDTO.setOpenid(user.getOpenid());
            wxMessageSendDTO.setAmount(amount);
            wxMessageSendDTO.setRemark(WARNING_REMARK);
            wxMessageSendDTO.setType(MessageTypeEnum.WARNING.getValue());
            wxService.sendMessage(wxMessageSendDTO);
        });
    }

}
