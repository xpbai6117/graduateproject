package com.xn.book.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xn.book.entity.BookBudgetInfo;

/**
 * <p>
 * 账本预算
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
public interface BookBudgetService extends IService<BookBudgetInfo> {


    Boolean saveOrUpdateBudget(BookBudgetInfo bookBudgetInfo);

    BookBudgetInfo bookBudgetInfo(Integer bookId);
}
