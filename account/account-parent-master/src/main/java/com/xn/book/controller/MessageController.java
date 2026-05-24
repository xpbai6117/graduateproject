package com.xn.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xn.book.common.response.BaseResult;
import com.xn.book.common.response.ResultUtils;
import com.xn.book.entity.Message;
import com.xn.book.enums.DataStatusEnum;
import com.xn.book.mapper.MessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */

@RestController
@RequestMapping("/api/message")
public class MessageController {


    @Autowired
    MessageMapper messageMapper;
    /**
     * 查询统计/user/send/apply
     */
    // TODO: 2022/4/5 0005  是否分页，待考虑
    @GetMapping("/get")
    public BaseResult<List<Message>> findList(@RequestParam(value = "type",required = false,defaultValue = "")String type) {
        List<Message> messages = messageMapper.selectList(new QueryWrapper<Message>().lambda()
                .eq(StringUtils.isNotBlank(type), Message::getType, type)
                .eq(Message::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        return ResultUtils.success(messages);

    }
}
