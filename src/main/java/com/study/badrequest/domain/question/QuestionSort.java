package com.study.badrequest.domain.question;

import com.study.badrequest.commons.response.ApiResponseStatus;

import com.study.badrequest.exception.custom_exception.RequestParamExceptionBasic;
import org.springframework.util.StringUtils;

public enum QuestionSort {

    NEW_EAST,
    RECOMMEND,
    VIEW;

    public static QuestionSort convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }

        for (QuestionSort sort : QuestionSort.values()) {
            if (sort.name().equalsIgnoreCase(source)) {
                return sort;
            }
        }

        throw new RequestParamExceptionBasic(ApiResponseStatus.NOT_EXIST_SORT_VALUE);
    }
}
