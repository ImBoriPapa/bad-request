package com.study.badrequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class TestController {

    @PostMapping("/api/image")
    public ResponseEntity formData(@RequestPart TestForm form,
                                   @RequestPart MultipartFile image) {
        log.info("test");

        return ResponseEntity.ok().body(TestResponse.builder()
                .name(form.getName())
                .age(form.getAge())
                .originalName(image.getOriginalFilename())
                .contentType(image.getContentType())
                .build());
    }

    @Getter
    @NoArgsConstructor
    public static class TestForm {
        private String name;
        private Integer age;
    }

    @Getter
    @NoArgsConstructor
    public static class TestResponse {
        private String name;
        private Integer age;
        private String originalName;
        private String contentType;

        @Builder
        public TestResponse(String name, Integer age, String originalName, String contentType) {
            this.name = name;
            this.age = age;
            this.originalName = originalName;
            this.contentType = contentType;
        }
    }
}
