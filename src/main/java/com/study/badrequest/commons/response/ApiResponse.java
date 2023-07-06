package com.study.badrequest.commons.response;


import com.study.badrequest.exception.BasicCustomException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ApiResponse {

    public static <T> ApiResponse.Success<T> success(T result){
        return new Success<>(ApiResponseStatus.SUCCESS,result);
    }

    public static <T> ApiResponse.Success<T> success(ApiResponseStatus status,T result){
        return new Success<>(status,result);
    }

    public static ApiResponse.Error error(Exception ex, String request){
        return new Error(ex, request);
    }

    public static ApiResponse.Error error(BasicCustomException ex, String request){
        return new Error(ex, request);
    }
    @NoArgsConstructor
    @Getter
    protected static class Success<T> {

        private String status;
        private int code;
        private String message;
        private T result;

        public Success(ApiResponseStatus status, T result) {
            this.status = status.name();
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
        private int code;
        private List<String> message;

        public Error(Exception ex, String request) {
            this.status = ApiResponseStatus.ERROR.name();
            this.requestPath = request;
            this.code = ApiResponseStatus.ERROR.getCode();
            this.message = List.of(ex.getMessage());
        }

        public Error(BasicCustomException ex, String request) {
            this.status = ex.getStatus().name();
            this.requestPath = request;
            this.code = ex.getErrorCode();
            this.message = ex.getErrorMessage();
        }
    }


}
