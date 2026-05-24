package com.xn.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum MessageTypeEnum implements EnumTemplate  {


    EARLY_WARNING(1, "耗尽预警"),


    WARNING(2, "超支告警");

    private final Integer value;

    private final String title;

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
