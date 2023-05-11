package com.study.badrequest.commons.response;


import com.study.badrequest.exception.BasicCustomException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ResponseForm {

    @NoArgsConstructor
    @Getter
    public static class Of<T> {
        private ApiResponseStatus status;
        private int code;
        private String message;
        private T result;

        public Of(ApiResponseStatus status, T result) {
            this.status = status;
            this.code = status.getCode();
            this.message = status.getMessage();
            this.result = result;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Error {
        private String status;
        private String requestPath;
        private int errorCode;
        private List<String> message;

        public Error(Exception ex, String request) {
            this.status = ApiResponseStatus.ERROR.name();
            this.requestPath = request;
            this.errorCode = ApiResponseStatus.ERROR.getCode();
            this.message = List.of(ex.getMessage());
        }

        public Error(BasicCustomException ex, String request) {
            this.status = ex.getStatus().name();
            this.requestPath = request;
            this.errorCode = ex.getErrorCode();
            this.message = ex.getErrorMessage();
        }
    }


}
