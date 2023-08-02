package com.study.badrequest.admin.query.interfaces;


import com.study.badrequest.common.response.ApiResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class SupportApiController {

    @GetMapping("/api/v2/admin/support/search/response-status")
    public ResponseEntity<?> searchApiStatusByCode(@RequestParam Integer code) {

        ApiResponseStatus customStatusByCode = ApiResponseStatus.findCustomStatusByCode(code);

        Response response = new Response(customStatusByCode);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @NoArgsConstructor
    @Getter
    public static class Response {
        private String status;
        private Integer code;
        private String message;
        private HttpStatus httpStatus;

        public Response(ApiResponseStatus status) {
            this.status = status.name();
            this.code = status.getCode();
            this.message = status.getMessage();
            this.httpStatus = status.getHttpStatus();
        }
    }
}
