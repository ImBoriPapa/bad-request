package com.study.badrequest.dto.board;

import com.study.badrequest.commons.annotation.EnumValid;
import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.board.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        private List<String> hashTags = new ArrayList<>();
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
