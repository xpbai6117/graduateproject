package com.xn.book.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xn.book.common.constant.ConstantPool;
import com.xn.book.common.exception.BasicInfoException;
import com.xn.book.common.exception.BasicInfoStatusEnum;
import com.xn.book.dto.WxMessageSendDTO;
import com.xn.book.entity.Book;
import com.xn.book.entity.BookMoney;
import com.xn.book.entity.BookUser;
import com.xn.book.entity.User;
import com.xn.book.enums.MessageTypeEnum;
import com.xn.book.response.UserLoginRespVO;
import com.xn.book.service.BookService;
import com.xn.book.service.BookUserService;
import com.xn.book.service.UserService;
import com.xn.book.service.WxService;
import com.xn.book.util.AccessTokenUtil;
import com.xn.book.util.ShortUuidGenerator;
import com.xn.book.vo.UserTokenVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WxServiceImpl implements WxService {

    private final String  jscode2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session?";

    private final String getPhoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessTokenUtil accessTokenUtil;

    @Autowired
    private BookUserService bookUserService;

    @Value("${wx.mnp.appid}")
    private String appId;

    @Value("${wx.mnp.secret}")
    private String secret;

    @Value("${wx.mnp.message.templateId.earlWarning}")
    private String earlWarning;

    @Value("${wx.mnp.message.templateId.warning}")
    private String warning;

    @Value("${wx.mnp.message.url}")
    private String messageUrl;

    @Autowired
    private BookService bookService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginRespVO auth(String jsCode) {

        String url = jscode2sessionUrl+"appid="+appId+"&secret="+secret+"&js_code="+jsCode+"&grant_type=authorization_code";
        ResponseEntity<String> out = restTemplate.getForEntity(url,String.class);
        JSONObject body = JSON.parseObject(out.getBody());
        log.info("-------body:"+body.toJSONString());
        Integer errcode = body.getInteger("errcode");
        if (errcode != null) {
            if (errcode != 0) {
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),body.getString("errmsg"));
            }
        }
        String openid = body.getString("openid");
        String sessionKey = body.getString("session_key");
        String unionid = body.getString("unionid");
        Map map = new HashMap();
        map.put("openid",openid);
        map.put("sesssionKey",sessionKey);
        map.put("unionid",unionid);

        User one = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));
        UserLoginRespVO userLoginRespVO = new UserLoginRespVO();
        if (StringUtils.isEmpty(one)){
            //创建新用户
            one = new User();
            one.setOpenid(openid);
            one.setCreateTime(new Date());
            one.setNickName("wx_" + ShortUuidGenerator.generateShortUuid(10));
            userService.save(one);

        }

        if (StringUtils.isEmpty(one.getNickName())){
            one.setNickName("wx_" + ShortUuidGenerator.generateShortUuid(10));
            userService.updateById(one);
        }

//        long count = bookService.count(new LambdaQueryWrapper<Book>()
//                .eq(Book::getCreateBy, one.getId())
//                .eq(Book::getStatus, 1));
//        if (count == 0){
//            //创建默认账本
//            Book book = new Book();
//            book.setBookName("生活账本");
//            book.setDefaultBook(1);
//            book.setCreateBy(one.getId());
//            book.setCreateTime(new Date());
//            bookService.addV2(book,one.getId());
//        }


        String token = IdUtil.fastSimpleUUID() + RandomUtil.randomString(8);

        UserTokenVO userTokenVO = new UserTokenVO().setUserId(one.getId()).setOpenid(openid)
                        .setToken(token);
        String tokenCacheKey = ConstantPool.getCacheTokenName() + StrUtil.COLON + userTokenVO.getToken();
        redisTemplate.opsForValue().set(tokenCacheKey, JSONUtil.toJsonStr(userTokenVO), 30, TimeUnit.DAYS);
        userLoginRespVO = BeanUtil.copyProperties(one, UserLoginRespVO.class);
        userLoginRespVO.setToken(token);
        return userLoginRespVO;
    }

    @Override
    public UserLoginRespVO bindPhone(String code,String token) {
        Object o = redisTemplate.opsForValue().get(ConstantPool.getCacheTokenName() + StrUtil.COLON + token);
        UserTokenVO bean = JSONUtil.toBean((String) o, UserTokenVO.class);
        String openid = bean.getOpenid();

        String accessToken = accessTokenUtil.getAccessToken(appId, secret);
        JSONObject phoneInfo = getPhoneNumber(code,accessToken);
        log.info("phoneInfo:"+phoneInfo);
        String phoneNumber = phoneInfo.get("phoneNumber").toString();

        //用户绑定
        User one = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));
        one.setUserAccount(phoneNumber);
        one.setNickName(phoneNumber);
        one.setTel(phoneNumber);
        one.setPwd("Abc" + phoneNumber.substring(phoneNumber.length()-4));
        userService.updateById(one);

        token = IdUtil.fastSimpleUUID() + RandomUtil.randomString(8);

        UserTokenVO userTokenVO = new UserTokenVO().setUserId(one.getId()).setOpenid(openid)
                .setToken(token);
        String tokenCacheKey = ConstantPool.getCacheTokenName() + StrUtil.COLON + userTokenVO.getToken();
        redisTemplate.opsForValue().set(tokenCacheKey, JSONUtil.toJsonStr(userTokenVO), 30, TimeUnit.DAYS);
        UserLoginRespVO userLoginRespVO = BeanUtil.copyProperties(one, UserLoginRespVO.class);
        userLoginRespVO.setToken(token);
        return userLoginRespVO;
    }


    public JSONObject getPhoneNumber(String code,String accessToken){
        String url = getPhoneUrl+accessToken;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        JSONObject in = new JSONObject();
        in.put("code",code);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(in,httpHeaders);
        ResponseEntity responseEntity = restTemplate.postForEntity(url,httpEntity,String.class);
        JSONObject body = JSON.parseObject(responseEntity.getBody().toString());
        Integer errcode = body.getInteger("errcode");
        if (errcode != null) {
            if (errcode != 0) throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),body.getString("errmsg"));
        }
        log.info("获取手机号wx返回参数："+body+",code:"+code);
        return body.getJSONObject("phone_info");
    }

    @Override
    public Boolean sendMessage(WxMessageSendDTO wxMessageSendDTO) {
        log.info("-------------send message in :"+wxMessageSendDTO.toString());

        String accessToken = accessTokenUtil.getAccessToken(appId,secret);
        //获取小程序全局唯一后台接口调用凭据（access_token）

        String url = messageUrl+accessToken;
        JSONObject in = new JSONObject();

        String templateId = "";
        Integer type = wxMessageSendDTO.getType();
        if (MessageTypeEnum.EARLY_WARNING.getValue().equals(type)){
            templateId = earlWarning;
        }else if (MessageTypeEnum.WARNING.getValue().equals(type)){
            templateId = warning;
        }else {
            log.info("当前类型不在处理范围");
            return false;
        }

        in.put("template_id",templateId);
        in.put("page",null);
        in.put("touser",wxMessageSendDTO.getOpenid());
        in.put("data",handleParam(wxMessageSendDTO));
        in.put("miniprogram_state","formal");
        in.put("lang","zh_CN");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,in,String.class);
        log.info("------wx message return :"+responseEntity+"-------");
        return true;
    }

    public JSONObject handleParam(WxMessageSendDTO wxMessageSendDTO){

        Integer type = wxMessageSendDTO.getType();
        JSONObject json = new JSONObject();

        if (MessageTypeEnum.EARLY_WARNING.getValue().equals(type)) {

            JSONObject phrase2 = new JSONObject();
            phrase2.put("value", "月预算");
            json.put("phrase2", phrase2);

            JSONObject amount3 = new JSONObject();
            amount3.put("value", wxMessageSendDTO.getAmount() + "元");
            json.put("amount3", amount3);

            JSONObject time1 = new JSONObject();
            time1.put("value", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            json.put("time1", time1);

            JSONObject thing4 = new JSONObject();
            thing4.put("value", wxMessageSendDTO.getRemark());
            json.put("thing4", thing4);
        }else if (MessageTypeEnum.WARNING.getValue().equals(type)){

            JSONObject thing1 = new JSONObject();
            thing1.put("value", "月预算");
            json.put("thing1", thing1);

            JSONObject amount2 = new JSONObject();
            amount2.put("value", wxMessageSendDTO.getAmount() + "元");
            json.put("amount2", amount2);

            JSONObject time3 = new JSONObject();
            time3.put("value", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            json.put("time3", time3);

            JSONObject thing4 = new JSONObject();
            thing4.put("value", wxMessageSendDTO.getRemark());
            json.put("thing4", thing4);
        }

        return json;
    }
}
