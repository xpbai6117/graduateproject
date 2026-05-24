package com.xn.book.service;

import com.xn.book.common.response.BaseResult;
import com.xn.book.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 账本表 服务类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
public interface BookService extends IService<Book> {

    /**
     * 更新账本
     *
     * @param book
     * @param userId
     */
    void update(Book book, Long userId);

    /**
     * 查询我的账本
     *
     * @return
     */
    List<Book> getBook();

    /**
     * 删除账本
     *
     * @param bookId
     */
    void delete(Long bookId);



    /**
     * 用户加入账本
     *
     * @param bookId
     */
    BaseResult userAddBook(Long bookId, String remark);

    /**
     * 判断用户是否有该账本权限
     * @param bookId
     */
    void isBookAuthor(Long bookId);

    /**
     * 添加账本
     * @param book
     */
    void add(Book book);

    void addV2(Book book,Long userId);


    BaseResult defaultBook(Long bookId, Long defaultBook);

    List<Book> searchBook(String bookName);

    BaseResult isBookMember(Long bookId);
}
