package com.study.badrequest.domain.board.entity;


import com.study.badrequest.exception.custom_exception.RequestParamException;
import lombok.Getter;
import org.springframework.util.StringUtils;

import static com.study.badrequest.commons.consts.CustomStatus.NOT_EXIST_CATEGORY;

@Getter
public enum Category {

    NOTICE("공지 사항 게시판"),
    QUESTION("질문 게시판"),
    KNOWLEDGE("지식 공유 게시판"),

    COMMUNITY("자유 게시판");

    private String explain;

    Category(String explain) {
        this.explain = explain;
    }

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
