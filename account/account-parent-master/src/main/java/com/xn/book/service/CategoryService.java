package com.xn.book.service;

import com.xn.book.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xn.book.request.CategoryQueryReqVo;
import com.xn.book.request.CategoryReqVo;
import com.xn.book.response.CategoryQueryResVo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 分类表 服务类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
public interface CategoryService extends IService<Category> {

    /**
     * 创建分类
     * @param categoryReqVo
     * @param s
     * @param bookId
     * @param request
     */
    void save(CategoryReqVo categoryReqVo, String s, @NotNull(message = "账本id不能为空") Long bookId, HttpServletRequest request);

    List<Category> findList(CategoryQueryReqVo categoryQueryReqVo);

    CategoryQueryResVo findBookIdList();

    void remove(Long categoryId);

}
