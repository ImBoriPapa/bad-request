package com.study.badrequest.domain.board;

import com.study.badrequest.exception.custom_exception.RequestParamExceptionBasic;
import lombok.Getter;
import org.springframework.util.StringUtils;

import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_EXIST_TOPIC;

@Getter
public enum Topic {

    JAVA("자바"),
    JAVASCRIPT("자바스크립트"),
    PYTHON("파이썬"),
    MYSQL("MySQL"),
    MONGODB("MongoDb");

    private String explain;

    Topic(String explain) {
        this.explain = explain;
    }

    public static Topic convert(String source) {

        if (!StringUtils.hasText(source)) {
            return null;
        }

        for (Topic t : Topic.values()) {

            if (t.name().equalsIgnoreCase(source)) {
                return t;
            }
        }

        throw new RequestParamExceptionBasic(NOT_EXIST_TOPIC);
    }

}
