package com.xn.book.controller;


import com.xn.book.common.response.BaseResult;
import com.xn.book.common.response.ResultUtils;
import com.xn.book.request.CategoryQueryReqVo;
import com.xn.book.response.CategoryQueryResVo;
import com.xn.book.service.BookCategoryService;
import com.xn.book.service.CategoryService;
import com.xn.book.request.CategoryReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 分类表 前端控制器
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    BookCategoryService bookCategoryService;

    /**
     * 新建账本分类
     *
     * @return
     */
    @PostMapping("/save")
    public BaseResult save(@Valid @RequestBody CategoryReqVo categoryReqVo, HttpServletRequest request) {
        categoryService.save(categoryReqVo,"/bookCategory",categoryReqVo.getBookId(),request);
        return ResultUtils.success();
    }


    /**
     * 新建账本分类
     *
     * @return
     */
    @GetMapping("/remove")
    public BaseResult remove(@RequestParam("categoryId") Long categoryId) {
        categoryService.remove(categoryId);
        return ResultUtils.success();
    }

    /**
     * 查询分类
     *
     * @return
     */
    @PostMapping("/findList")
    public void findList(@Valid @RequestBody CategoryQueryReqVo categoryQueryReqVo) {
        categoryService.findList(categoryQueryReqVo);
    }


    /**
     * 根据bookId查询分类
     *
     * @return
     */
    @GetMapping("/findBookIdList")
    public CategoryQueryResVo findBookIdList() {
        return categoryService.findBookIdList();
    }
}
