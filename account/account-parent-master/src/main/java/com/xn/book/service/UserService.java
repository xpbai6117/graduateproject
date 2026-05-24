package com.xn.book.service;

import com.xn.book.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xn.book.request.EditAvatarDTO;
import com.xn.book.request.UserLoginReqVO;
import com.xn.book.request.UserNameReqVo;
import com.xn.book.response.UserLoginRespVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 用户表（微信）表 服务类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userLoginReqVO
     * @return
     */
    UserLoginRespVO userLogin(UserLoginReqVO userLoginReqVO);

    UserLoginRespVO userLoginV2(String username,String pwd);

    /**
     * 修改用户别名,真实姓名
     * @param userNameReqVo
     * @return
     */
    void updateReallyNameAndUserName( UserNameReqVo userNameReqVo);


    Boolean register(String username,String pwd,String pwdConfirm,String openId);

    UserLoginRespVO editUserName(String nickName);

    UserLoginRespVO editAvatar(@RequestBody EditAvatarDTO editAvatarDTO);
}
