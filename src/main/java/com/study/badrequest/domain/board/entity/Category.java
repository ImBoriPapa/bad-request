package com.study.badrequest.domain.board.entity;


import com.study.badrequest.exception.custom_exception.RequestParamException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import static com.study.badrequest.commons.consts.CustomStatus.NOT_EXIST_CATEGORY;

@Getter
public enum Category {

    NOTICE,
    QUESTION,
    KNOWLEDGE,

    INFORMATION;

    public static Category convert(String source) {

        if (!StringUtils.hasText(source)) {
            return null;
        }

        for (Category c : Category.values()) {

            if (c.name().equalsIgnoreCase(source)) {
                return c;
            }
        }

        throw new RequestParamException(NOT_EXIST_CATEGORY);
    }
}
