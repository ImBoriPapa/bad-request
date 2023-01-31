package com.study.badrequest.domain.board.dto;

import com.study.badrequest.aop.annotation.EnumValid;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
public class BoardRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {
        @NotNull(message = "회원 식별자는 필수 입니다.")
        private Long memberId;
        @NotBlank(message = "제목은 필수 입니다.")
        private String title;
        @NotBlank(message = "내용은 반드시 입력하여야 합니다.")
        private String context;
        @EnumValid(message = "주제는 필수입니다.", enumClass = Topic.class)
        private Topic topic;
        @EnumValid(message = "카테고리는 필수입니다.", enumClass = Category.class)
        private Category category;
    }
}
