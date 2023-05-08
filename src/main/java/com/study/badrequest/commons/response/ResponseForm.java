package com.study.badrequest.commons.response;


import com.study.badrequest.exception.BasicException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ResponseForm {

    @NoArgsConstructor
    @AllArgsConstructor
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
    @AllArgsConstructor
    @Getter
    public static class Error<T> {
        private String status;
        private String requestPath;
        private int errorCode;
        private List message;

        public Error(Exception ex, String request) {
            this.status = ApiResponseStatus.ERROR.name();
            this.requestPath = request;
            this.errorCode = ApiResponseStatus.ERROR.getCode();
            this.message = List.of(ex.getMessage());
        }

        public Error(BasicException ex, String request) {
            this.status = ex.getStatus();
            this.requestPath = request;
            this.errorCode = ex.getErrorCode();
            this.message = ex.getErrorMessage();
        }
    }


}
