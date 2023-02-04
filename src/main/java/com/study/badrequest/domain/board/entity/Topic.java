package com.study.badrequest.domain.board.entity;

import com.study.badrequest.exception.custom_exception.RequestParamException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import static com.study.badrequest.commons.consts.CustomStatus.NOT_EXIST_TOPIC;

@Getter
public enum Topic {

    JAVA,
    JAVASCRIPT,
    PYTHON,
    MYSQL,
    MONGODB;

    public static Topic convert(String source) {

        if (!StringUtils.hasText(source)) {
            return null;
        }

        for (Topic t : Topic.values()) {

            if (t.name().equalsIgnoreCase(source)) {
                return t;
            }
        }

        throw new RequestParamException(NOT_EXIST_TOPIC);
    }

}
