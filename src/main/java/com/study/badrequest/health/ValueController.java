package com.study.badrequest.health;

import com.study.badrequest.commons.consts.CustomStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ValueController {

    public static final String CUSTOM_STATUS = "/api/v1/custom-status";


    @GetMapping(CUSTOM_STATUS)
    public ResponseEntity getStatus() {

        return ResponseEntity.ok()
                .body(Arrays.stream(CustomStatus.values()).map(StatusDto::new).collect(Collectors.toList()));
    }

    @Getter
    @NoArgsConstructor
    public static class StatusDto {
        private String status;
        private int code;
        private String message;

        public StatusDto(CustomStatus customStatus) {
            this.status = customStatus.name();
            this.code = customStatus.getCode();
            this.message = customStatus.getMessage();
        }
    }
}
