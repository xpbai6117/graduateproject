package com.xn.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xn.book.entity.BookUser;
import com.xn.book.enums.BookUserAuditStatusEnum;
import com.xn.book.enums.DataStatusEnum;
import com.xn.book.service.BookUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplaceDataApi {

    @Autowired
    BookUserService bookUserService;

    public List<BookUser> replaceList(Long bookId) {
        return   bookUserService.list(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId)
                .ne(BookUser::getAuditStatus, BookUserAuditStatusEnum.REJECT.getValue())
                .orderByDesc(BookUser::getAuth)
        );
    }

}
