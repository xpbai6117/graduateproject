package com.xn.book.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 【请填写功能名称】resp对象 program_wx_pay_serial
 * 
 * @author ruoyi
 * @date 2022-08-18
 */

@Data
@ApiModel(value = "【请填写功能名称】返回值")
public class ProgramWxPaySerialRespDTO implements Serializable
{
    private static final long serialVersionUID = 1L;

    
    private Long id;
        
    /** 商户id */
    @ApiModelProperty("商户id")
    private String merchantId;
        
    /** 微信支付平台证书序列号 */
    @ApiModelProperty("微信支付平台证书序列号")
    private String serialNo;
        
    /** 微信商户id */
    @ApiModelProperty("微信商户id")
    private String mchId;
        
    /** appid */
    @ApiModelProperty("appid")
    private String appId;
        
    /** 有效标识（0：无效；1：有效） */
    @ApiModelProperty("有效标识（0：无效；1：有效）")
    private String state;
        
    /** secret */
    @ApiModelProperty("secret")
    private String secret;
        
    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private Date createTime;
        
    /** 私钥 */
    @ApiModelProperty("私钥")
    private String privateKey;
        
    /** apiV3Key */
    @ApiModelProperty("apiV3Key")
    private String v3Key;
    }