package com.xn.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "book_budget_info")
public class BookBudgetInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "账本不能为空")
    private Integer bookId;

    private Integer year;

    private Integer month;

    private Long budget;

    private Integer status;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
