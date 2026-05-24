package com.xn.book.controller;


import com.xn.book.request.EditAvatarDTO;
import com.xn.book.request.UserLoginReqVO;
import com.xn.book.request.UserNameReqVo;
import com.xn.book.response.UserLoginRespVO;
import com.xn.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 用户表（微信）表 前端控制器
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录
     *
     * @param reqVo
     * @return
     */
    @PostMapping("/login")
    public UserLoginRespVO login(@RequestBody UserLoginReqVO reqVo) {
        return userService.userLogin(reqVo);
    }

    @GetMapping("/loginV2")
    public UserLoginRespVO loginV2(@RequestParam("username") String username,@RequestParam("pwd") String pwd) {
        return userService.userLoginV2(username,pwd);
    }

    @GetMapping("register")
    public Boolean register(@RequestParam("username") String username,@RequestParam("pwd") String pwd,@RequestParam("pwdConfirm") String pwdConfirm,@RequestParam("openId") String openId){
        return userService.register(username,pwd,pwdConfirm,openId);
    }

    /**
     * 修改用户别名，真实姓名
     *
     * @param userNameReqVo
     * @return
     */
    @GetMapping("/login")
    public void updateReallyNameAndUserName(@Valid @RequestBody  UserNameReqVo userNameReqVo) {
        userService.updateReallyNameAndUserName(userNameReqVo);
    }

    @GetMapping("edit/nickName")
    public UserLoginRespVO editUserName(@RequestParam("nickName") String nickName){
        return userService.editUserName(nickName);
    }

    @PostMapping("avatar")
    public UserLoginRespVO editAvatar(EditAvatarDTO editAvatarDTO){
        return userService.editAvatar(editAvatarDTO);
    }
}
