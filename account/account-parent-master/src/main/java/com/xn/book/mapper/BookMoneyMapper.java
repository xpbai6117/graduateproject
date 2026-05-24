package com.xn.book.mapper;

import com.xn.book.entity.BookMoney;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xn.book.request.BookMoneyQueryReqVo;
import com.xn.book.request.UserMoneyStatisticsRequestVo;
import com.xn.book.response.UserMoneyStatisticsRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 账本记账金额表 Mapper 接口
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
public interface BookMoneyMapper extends BaseMapper<BookMoney> {

    /**
     * 用户头像等信息存在是，取代分类头像等信息
     *
     * @param bookMoneyQueryReqVo
     * @return
     */
    List<BookMoney> listMoney(@Param("bookMoneyQueryReqVo") BookMoneyQueryReqVo bookMoneyQueryReqVo);

    List<UserMoneyStatisticsRes> findUserStatistics(@Param("statistics") UserMoneyStatisticsRequestVo userMoneyStatisticsRequestVo);
}
