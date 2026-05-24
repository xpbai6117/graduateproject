package com.xn.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xn.book.common.context.UserTokenContextHolder;
import com.xn.book.common.exception.BasicInfoException;
import com.xn.book.common.exception.BasicInfoStatusEnum;
import com.xn.book.entity.BookMoney;
import com.xn.book.entity.Category;
import com.xn.book.enums.BookMoneyTypeEnum;
import com.xn.book.enums.DataStatusEnum;
import com.xn.book.mapper.BookMoneyMapper;
import com.xn.book.mapper.CategoryMapper;
import com.xn.book.request.CategoryQueryReqVo;
import com.xn.book.response.CategoryQueryResVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xn.book.request.CategoryReqVo;
import com.xn.book.service.BookCategoryService;
import com.xn.book.service.BookService;
import com.xn.book.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 分类表 服务实现类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    BookCategoryService bookCategoryService;
    @Autowired
    BookService bookService;



    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    BookMoneyMapper bookMoneyMapper;

    @Override
    @Transactional
    public void save(CategoryReqVo categoryReqVo, String uploadPath, Long bussId, HttpServletRequest request) {

        // 获取用户id
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        List<Category> categoryIds = categoryMapper.selectList(new QueryWrapper<Category>().lambda()
                .eq(Category::getBookId, categoryReqVo.getBookId())
                .eq(Category::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(Category::getName, categoryReqVo.getName()));
        if (categoryIds.size() != 0) {
            // 查询是否存在相同分类名称
            logger.error("相同账本存在相同名称:{}", categoryReqVo.getName());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "分类名已经存在");
        }
        Long bookId = categoryReqVo.getBookId();
        bookService.isBookAuthor(bookId);
       // List<String> uploads = uploadFileService.uploads(categoryReqVo.getIcon(), uploadPath, String.valueOf(bussId), request);
        Category category = new Category();
        BeanUtils.copyProperties(categoryReqVo, category);
        // 设置创建时间+创建人
        category.setCreateTime(new Date());
        category.setCreateBy(userId);
        category.setStatus("1");
        category.setIcon(categoryReqVo.getIcon());
        category.setType(categoryReqVo.getType());
        this.save(category);

    }

    @Override
    public void remove(Long categoryId) {
        // 根据分类查询账本id，
        Category category = categoryMapper.selectOne(new QueryWrapper<Category>().lambda()
                .eq(Category::getStatus, "1")
                .eq(Category::getId, categoryId));
        if (null == category) {
            // 查询是否存在相同分类名称
            logger.error("该分类已经被删除，分类id：{}", categoryId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "该分类不存在");
        }
        Long count = bookMoneyMapper.selectCount(new QueryWrapper<BookMoney>().lambda()
                .eq(BookMoney::getCategoryId, categoryId)
                .eq(BookMoney::getStatus, "1")
        );
        // 该分类已经被记账（tb_book_money）选择，不能删除
        if (count.intValue() != 0) {
            logger.error("该分类已经被使用，不能删除，分类id：{}", categoryId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "分类已使用，不能删除");
        }

        // 检验是否拥有账本权限
        bookService.isBookAuthor(category.getBookId());
        // 删除（禁用）
        category.setStatus("0");
        categoryMapper.updateById(category);
    }

    @Override
    public List<Category> findList(CategoryQueryReqVo categoryQueryReqVo) {
        return this.list(new QueryWrapper<Category>().lambda()
                .eq(Category::getBookId, categoryQueryReqVo.getBookId())
                .eq(Category::getStatus, "1")
                .eq(Category::getType, categoryQueryReqVo.getType())
                .orderByAsc(Category::getSort));
    }

    @Override
    public CategoryQueryResVo findBookIdList() {
        List<Category> list = this.list(new QueryWrapper<Category>().lambda()
                .eq(Category::getStatus, "1")
                .orderByAsc(Category::getSort)
        );
        List<Category> srCategory = list.stream().filter(x -> BookMoneyTypeEnum.EXPENDITURE.getValue().intValue() == x.getType().intValue()).collect(Collectors.toList());
        List<Category> zcCategory = list.stream().filter(x -> BookMoneyTypeEnum.INCOME.getValue().intValue() == x.getType().intValue()).collect(Collectors.toList());
        return new CategoryQueryResVo(srCategory, zcCategory);
    }
}
