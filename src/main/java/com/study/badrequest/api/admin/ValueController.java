package com.study.badrequest.api.admin;

import com.study.badrequest.commons.response.ApiResponseStatus;

import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.board.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.study.badrequest.api.admin.ValueController.VALUES;

@RestController
@Slf4j
@RequestMapping(VALUES)
public class ValueController {

    public static final String VALUES = "/api/v1/values";
    public static final String STATUS = "/status";
    public static final String CATEGORY = "/category";
    public static final String TOPIC = "/topic";


    @GetMapping(STATUS)
    public ResponseEntity getStatus() {

        return ResponseEntity.ok()
                .body(Arrays.stream(ApiResponseStatus.values()).map(StatusDto::new).collect(Collectors.toList()));
    }

    @Getter
    @NoArgsConstructor
    static class StatusDto {
        private String status;
        private int code;
        private String message;

        public StatusDto(ApiResponseStatus apiResponseStatus) {
            this.status = apiResponseStatus.name();
            this.code = apiResponseStatus.getCode();
            this.message = apiResponseStatus.getMessage();
        }
    }

    @GetMapping(CATEGORY)
    public ResponseEntity getCategory() {

        return ResponseEntity.ok()
                .body(Arrays.stream(Category.values()).map(CategoryDto::new).collect(Collectors.toList()));
    }

    @Getter
    @NoArgsConstructor
    static class CategoryDto {
        private String category;
        private String explain;

        public CategoryDto(Category category) {
            this.category = category.name();
            this.explain = category.getExplain();
        }
    }

    @GetMapping(TOPIC)
    public ResponseEntity getTopic() {

        return ResponseEntity.ok()
                .body(Arrays.stream(Topic.values()).map(TopicDto::new).collect(Collectors.toList()));
    }

    @Getter
    @NoArgsConstructor
    static class TopicDto {
        private String topic;
        private String explain;

        public TopicDto(Topic topic) {
            this.topic = topic.name();
            this.explain = topic.getExplain();
        }
    }

}
