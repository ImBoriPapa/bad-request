package com.study.badrequest.domain.question;

import com.study.badrequest.commons.response.ApiResponseStatus;

import com.study.badrequest.exception.CustomRuntimeException;
import org.springframework.util.StringUtils;

public enum QuestionSortType {

    NEW_EAST,
    RECOMMEND,
    VIEW;

    public static QuestionSortType convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }

        for (QuestionSortType sort : QuestionSortType.values()) {
            if (sort.name().equalsIgnoreCase(source)) {
                return sort;
            }
        }

        throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_EXIST_SORT_VALUE);
    }
}
