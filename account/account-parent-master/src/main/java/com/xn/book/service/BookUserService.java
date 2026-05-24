package com.xn.book.service;

import com.xn.book.common.response.BaseResult;
import com.xn.book.entity.BookUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xn.book.request.UserApplyBookReqVo;
import com.xn.book.response.BookUserReqVo;
import com.xn.book.vo.BookUserEditVo;

import java.util.List;

/**
 * <p>
 * 账本用户表 服务类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
public interface BookUserService extends IService<BookUser> {

    /**
     * 查询账本下的用户列表
     *
     * @param bookId
     * @return
     */
    BookUserReqVo getBookUser(Long bookId);


    /**
     * 添加成员为管理员
     *
     * @param bookId
     * @param userId
     * @return
     */
    BaseResult addAuth(Long bookId, Long userId);

    /**
     * 移除管理员
     *
     * @param bookId
     * @param userId
     * @return
     */
    BaseResult removeAuth(Long bookId, Long userId);

    /**
     * 删除成员
     *
     * @param bookId
     * @param userId
     * @return
     */
    Object remove(Long bookId, Long userId);

    /**
     * 编辑成员
     *
     * @param bookUserEditVo
     * @return
     */
    Object editBookUser(BookUserEditVo bookUserEditVo);


    BaseResult update(BookUserEditVo bookUserEditVo);

    Object signOutBook(Long bookId);


    /**
     * 查询账本申请加入的用户
     */
    Object bookAudit(Long bookId);

    void reject(UserApplyBookReqVo userApplyBookReqVo);

    void agree(UserApplyBookReqVo userApplyBookReqVo);

    Integer getAuditCount();


    Object getAuditAll();

    Object myApply();

    BaseResult sendApply(UserApplyBookReqVo userApplyBookReqVo);


}
