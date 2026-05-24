package com.xn.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xn.book.common.context.UserTokenContextHolder;
import com.xn.book.common.exception.BasicInfoException;
import com.xn.book.entity.*;
import com.xn.book.enums.*;
import com.xn.book.entity.Book;
import com.xn.book.mapper.BookBudgetMapper;
import com.xn.book.service.*;
import com.xn.book.entity.BookBudgetInfo;
import com.xn.book.service.BookBudgetService;
import com.xn.book.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 账本表 服务实现类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
@Service
public class BookBudgetServiceImpl extends ServiceImpl<BookBudgetMapper, BookBudgetInfo> implements BookBudgetService {

    private static final Logger logger = LoggerFactory.getLogger(BookBudgetServiceImpl.class);

    @Autowired
    private BookService bookService;

    @Override
    public Boolean saveOrUpdateBudget(BookBudgetInfo bookBudgetInfo) {

        Integer bookId = bookBudgetInfo.getBookId();
        Book book = bookService.getById(bookId);
        if (StringUtils.isEmpty(book))
            throw new BasicInfoException(500,"当前账本不存在");
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        if (!userId.equals(book.getCreateBy()))
            throw new BasicInfoException(500,"当前用户没有此账本");

        Integer id = bookBudgetInfo.getId();
        if (StringUtils.isEmpty(id)){
            LocalDateTime now = LocalDateTime.now();
            bookBudgetInfo.setYear(now.getYear());
            bookBudgetInfo.setMonth(now.getMonthValue());
            bookBudgetInfo.setCreateTime(new Date());
            bookBudgetInfo.setCreateBy(userId);
            baseMapper.insert(bookBudgetInfo);
        }else {
            BookBudgetInfo bookBudgetInfo1 = baseMapper.selectById(id);
            BeanUtils.copyProperties(bookBudgetInfo,bookBudgetInfo1);
            bookBudgetInfo1.setUpdateBy(userId);
            bookBudgetInfo1.setUpdateTime(new Date());
            baseMapper.updateById(bookBudgetInfo1);
        }
        return true;
    }

    @Override
    public BookBudgetInfo bookBudgetInfo(Integer bookId) {
        BookBudgetInfo bookBudgetInfo = baseMapper.selectOne(new LambdaQueryWrapper<BookBudgetInfo>()
                .eq(BookBudgetInfo::getBookId, bookId));
        if (!StringUtils.isEmpty(bookBudgetInfo)){
            return bookBudgetInfo;
        }
        return null;
    }
}
