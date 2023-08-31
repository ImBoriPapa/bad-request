package com.study.badrequest.question.command.domain.values;

import com.study.badrequest.common.response.ApiResponseStatus;

import com.study.badrequest.common.exception.CustomRuntimeException;
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
