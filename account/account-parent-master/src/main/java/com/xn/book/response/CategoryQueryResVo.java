package com.xn.book.response;


import com.xn.book.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 支出收入类型集合返回
 * 2022-02-13 17:17
 */
@Data
@AllArgsConstructor
public class CategoryQueryResVo {


    /**
     * 收入
     */
    List<Category> srCategory;


    /**
     * 支出
     */
    List<Category> zcCategory;
}
