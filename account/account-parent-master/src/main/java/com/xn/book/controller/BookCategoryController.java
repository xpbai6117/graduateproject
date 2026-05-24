package com.xn.book.controller;


import com.xn.book.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 账本分类表 前端控制器
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/bookCategory")
public class BookCategoryController {


    @Autowired
    BookCategoryService bookCategoryService;

}
