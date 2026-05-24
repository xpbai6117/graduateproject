package com.xn.book.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xn.book.common.constant.CacheConstant;
import com.xn.book.common.constant.ConstantPool;
import com.xn.book.common.context.UserTokenContextHolder;
import com.xn.book.common.exception.BasicInfoException;
import com.xn.book.common.exception.BasicInfoStatusEnum;
import com.xn.book.dto.UserDTO;
import com.xn.book.dto.UserLoginDTO;
import com.xn.book.entity.BookUser;
import com.xn.book.entity.User;
import com.xn.book.mapper.UserMapper;
import com.xn.book.request.EditAvatarDTO;
import com.xn.book.request.UserNameReqVo;
import com.xn.book.service.BookUserService;
import com.xn.book.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xn.book.request.UserLoginReqVO;
import com.xn.book.response.UserLoginRespVO;
import com.xn.book.vo.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表（微信）表 服务实现类
 * </p>
 *
 * @author xn
 * @since 2022-04-02
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @Autowired
    BookUserService bookUserService;

    @Override
    public UserLoginRespVO userLogin(UserLoginReqVO userLoginReqVO) {

        // 请求微信 auth.code2Session 接口，换取用户唯一标识 openid
        String openid = "";
        String sessionKey = "";
        try {
            WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMaService.getUserService()
                    .getSessionInfo(userLoginReqVO.getCode());
            openid = wxMaJscode2SessionResult.getOpenid();
            sessionKey = wxMaJscode2SessionResult.getSessionKey();
        } catch (WxErrorException e) {
            log.error("请求微信接口code2Session，获取用户唯一标识 openid异常:{}", e.fillInStackTrace());
            throw new BasicInfoException(BasicInfoStatusEnum.THIRD_SERVICE_UNAVAILABLE.getCode(), "请求微信服务失败");
        }

        UserLoginDTO userLoginDTO = new UserLoginDTO().setOpenid(openid);

        // 校验和解密用户信息
        if (StrUtil.isNotBlank(userLoginReqVO.getRawData())
                && StrUtil.isNotBlank(userLoginReqVO.getSignature())
                && StrUtil.isNotBlank(userLoginReqVO.getIv())
                && StrUtil.isNotBlank(userLoginReqVO.getEncryptedData())) {


            // 用户信息校验
            Assert.isTrue(
                    wxMaService.getUserService().checkUserInfo(sessionKey, userLoginReqVO.getRawData(),
                            userLoginReqVO.getSignature()),
                    () -> new BasicInfoException(BasicInfoStatusEnum.PARAM_ERROR.getCode(), "用户信息校验失败"));

            // 解密用户信息
            WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey,
                    userLoginReqVO.getEncryptedData(), userLoginReqVO.getIv());

            userLoginDTO.setAvatarUrl(userInfo.getAvatarUrl()).setNickName(userInfo.getNickName())
                    .setGender(StrUtil.isNotBlank(userInfo.getGender()) ? Integer.valueOf(userInfo.getGender()) : 0);

        }

        UserDTO userDTO = userLogin(userLoginDTO);

        // 用户重新登录后，移除用户历史token和历史sessionKey
        String historyToken = (String) redisTemplate.opsForValue()
                .get(ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.OPENID_KEY + openid);
        if (StrUtil.isNotBlank(historyToken)) {
            redisTemplate.delete(ConstantPool.getCacheTokenName() + StrUtil.COLON + historyToken);
            redisTemplate.delete(
                    ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.USER_SESSION_KEY + historyToken);
        }
        redisTemplate.opsForValue().set(
                ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.OPENID_KEY + openid, userDTO.getToken(),
                30, TimeUnit.DAYS);

        // token 保存到缓存，有效时间30天
        UserTokenVO userTokenVO = new UserTokenVO().setUserId(userDTO.getId()).setOpenid(openid)
                .setToken(userDTO.getToken());
        String tokenCacheKey = ConstantPool.getCacheTokenName() + StrUtil.COLON + userTokenVO.getToken();
        redisTemplate.opsForValue().set(tokenCacheKey, JSONUtil.toJsonStr(userTokenVO), 30, TimeUnit.DAYS);

        // sessionKey 保存到缓存，有效时间3天
        String sessionKeyCacheKey = ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.USER_SESSION_KEY
                + userTokenVO.getToken();
        redisTemplate.opsForValue().set(sessionKeyCacheKey, sessionKey, 3, TimeUnit.DAYS);
        UserLoginRespVO userLoginRespVO = BeanUtil.copyProperties(userDTO, UserLoginRespVO.class);
        return userLoginRespVO;
    }

    @Override
    public UserLoginRespVO userLoginV2(String username, String pwd) {


        User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getUserAccount,username));

        if (StringUtils.isEmpty(user))
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),"账号或密码错误");
        if (!user.getPwd().equals(pwd))
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),"账号或密码错误");

        String openid = user.getOpenid();
        String token = IdUtil.fastSimpleUUID() + RandomUtil.randomString(8);


        // 用户重新登录后，移除用户历史token和历史sessionKey
        String historyToken = (String) redisTemplate.opsForValue()
                .get(ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.OPENID_KEY + openid);
        if (StrUtil.isNotBlank(historyToken)) {
            redisTemplate.delete(ConstantPool.getCacheTokenName() + StrUtil.COLON + historyToken);
            redisTemplate.delete(
                    ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.USER_SESSION_KEY + historyToken);
        }
        redisTemplate.opsForValue().set(
                ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.OPENID_KEY + openid, token,
                30, TimeUnit.DAYS);

        // token 保存到缓存，有效时间30天
        UserTokenVO userTokenVO = new UserTokenVO().setUserId(user.getId()).setOpenid(openid)
                .setToken(token);
        String tokenCacheKey = ConstantPool.getCacheTokenName() + StrUtil.COLON + userTokenVO.getToken();
        redisTemplate.opsForValue().set(tokenCacheKey, JSONUtil.toJsonStr(userTokenVO), 30, TimeUnit.DAYS);

        // sessionKey 保存到缓存，有效时间3天
//        String sessionKeyCacheKey = ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.USER_SESSION_KEY
//                + userTokenVO.getToken();
//        redisTemplate.opsForValue().set(sessionKeyCacheKey, sessionKey, 3, TimeUnit.DAYS);
        UserLoginRespVO userLoginRespVO = BeanUtil.copyProperties(user, UserLoginRespVO.class);
        userLoginRespVO.setToken(token);
        return userLoginRespVO;
    }

    public UserDTO userLogin(UserLoginDTO userLoginDTO) {
        User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getOpenid, userLoginDTO.getOpenid()));
        boolean bool = false;
        if (null != user) {
            user.setAvatarUrl(StrUtil.isNotBlank(userLoginDTO.getAvatarUrl()) ? userLoginDTO.getAvatarUrl()
                    : user.getAvatarUrl());
            user.setGender(Objects.nonNull(userLoginDTO.getGender()) ? userLoginDTO.getGender() : user.getGender());
            user.setNickName(
                    StrUtil.isNotBlank(userLoginDTO.getNickName()) ? userLoginDTO.getNickName() : user.getNickName());
        } else {
            bool = true;
            user = new User();
            BeanUtil.copyProperties(userLoginDTO, user, false);
        }

        if (StrUtil.isNotBlank(user.getNickName())) {
            user.setNickName(user.getNickName().trim());
        }
        user.setCreateTime(new Date());
        if (bool) {
            userService.save(user);
        } else {
            userService.updateById(user);
        }
        String token = IdUtil.fastSimpleUUID() + RandomUtil.randomString(8);

        return BeanUtil.copyProperties(user, UserDTO.class).setToken(token);
    }


    /**
     * 修改用户别名,真实姓名
     *
     * @param userNameReqVo
     */
    @Override
    public void updateReallyNameAndUserName(UserNameReqVo userNameReqVo) {
        User byId = this.getById(userNameReqVo.getBookId());
        if (byId == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.PARAM_ERROR.getCode(), "用户不存在");
        }

        UserTokenVO userTokenVOByToken = UserTokenContextHolder.getUserTokenVOByToken();

        // 是否是本人操作
        boolean isUser = userTokenVOByToken.getOpenid().equals(byId.getOpenid());

        // 操作人是否是管理员
        BookUser bookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, userNameReqVo.getBookId())
                .eq(BookUser::getUserId, userNameReqVo.getUserId()));
        if(null==bookUser){
            throw new BasicInfoException(BasicInfoStatusEnum.PARAM_ERROR.getCode(), "账本不存在该用户");
        }
        // 是否是管理员权限
        boolean isRoot = "1".equals(bookUser.getAuth());

        if(isRoot||isUser){
            bookUser.setUpdateBy(byId.getId());
            bookUser.setReallyName(userNameReqVo.getReallyName());
            bookUser.setUserName(userNameReqVo.getUserName());
            bookUserService.updateById(bookUser);
        }

    }

    @Override
    public Boolean register(String username, String pwd, String pwdConfirm,String openId) {
        User one = userService.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getUserAccount, username));
        if (!StringUtils.isEmpty(one))
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),"注册失败");
        if (!pwd.equals(pwdConfirm))
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),"两次输入的密码不一致");
        User one1 = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openId));
        if (StringUtils.isEmpty(one1)) {
            one = new User();
            one.setUserAccount(username);
            one.setNickName(username);
            one.setPwd(pwd);
            one.setOpenid(openId);
            userService.save(one);
        }else {
            one1.setUserAccount(username);
            one1.setPwd(pwd);
            one1.setNickName(username);
            one1.setUpdateTime(new Date());
            userService.updateById(one1);
        }
        return true;
    }

    @Override
    public UserLoginRespVO editUserName(String nickName) {
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        if (userId == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "请登录");
        }
        User userInfo = userService.getById(userId);
        if (userInfo == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "请登录");
        }

        userInfo.setNickName(nickName);
        userInfo.setUpdateTime(new Date());
        userService.updateById(userInfo);

        UserLoginRespVO userLoginRespVO = BeanUtil.copyProperties(userInfo, UserLoginRespVO.class);
        return userLoginRespVO;
    }

    @Override
    public UserLoginRespVO editAvatar(EditAvatarDTO editAvatarDTO) {
        User user = baseMapper.selectById(editAvatarDTO.getUserId());

        if (!StringUtils.isEmpty(user)){
            MultipartFile file = editAvatarDTO.getFile();
            String s = updateUserAvatar(file);
            log.info("MultipartFile base64:" + s);
            user.setAvatarUrl("data:image/png;base64," + s);
            user.setUpdateTime(new Date());
            baseMapper.updateById(user);
        }
        UserLoginRespVO userLoginRespVO = BeanUtil.copyProperties(user, UserLoginRespVO.class);

        return userLoginRespVO;
    }

    private byte[] convertToByteArray(MultipartFile file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return bytes;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public String updateUserAvatar(MultipartFile file) {
        //将file文件转为Base64
        byte[] bytes = new byte[0];
        try {
            bytes = this.convertToByteArray(file);
        } catch (IOException e) {
            return "";
        }
        String avatar = Base64.getEncoder().encodeToString(bytes);

        return avatar;
    }
}
