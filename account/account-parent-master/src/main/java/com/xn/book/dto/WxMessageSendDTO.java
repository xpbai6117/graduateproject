
package com.xn.book.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WxMessageSendDTO {

    /**
     * 1:耗尽预警，2:超支预警
     */
    private Integer type;

    private String openid;

    //private String data;

   // private String merchantId;

    private BigDecimal amount;

    private String remark;

    private String bookName;

    private Integer year;

    private Integer month;

}
