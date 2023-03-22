package com.study.badrequest.domain.board.dto;

import com.study.badrequest.aop.annotation.EnumValid;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class BoardRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Create {

        @NotBlank(message = "제목은 필수 입니다.")
        private String title;
        @NotBlank(message = "내용은 반드시 입력하여야 합니다.")
        private String contents;
        @EnumValid(message = "주제는 필수입니다.", enumClass = Topic.class)
        private Topic topic;
        @EnumValid(message = "카테고리는 필수입니다.", enumClass = Category.class)
        private Category category;
        private List<Long> imageIds = new ArrayList<>();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Update {

        private String title;
        private String contents;
        private Topic topic;
        private Category category;
        private List<Long> imageId;

    }
}
