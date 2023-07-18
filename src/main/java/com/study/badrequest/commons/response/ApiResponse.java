package com.study.badrequest.commons.response;


import com.study.badrequest.exception.CustomRuntimeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse {

    public static <T> ApiResponse.Success<T> success(T result) {
        return new Success<>(ApiResponseStatus.SUCCESS, result);
    }

    public static <T> ApiResponse.Success<T> success(ApiResponseStatus status, T result) {
        return new Success<>(status, result);
    }

    public static ApiResponse.Error error(Exception exception, String requestedPath) {
        final ApiResponseStatus error = ApiResponseStatus.SERVER_ERROR;

        final String status = error.name();
        final int errorCode = error.getCode();
        final String message = exception.getMessage() != null ? exception.getMessage() : ApiResponseStatus.SERVER_ERROR.getMessage();

        return new Error(status, errorCode, message, requestedPath);
    }

    public static ApiResponse.Error error(CustomRuntimeException customRuntimeException, String requestedPath) {

        final String status = customRuntimeException.getStatus();
        final int errorCode = customRuntimeException.getErrorCode();
        final String message = customRuntimeException.getErrorMessage();

        return new Error(status, errorCode, message, requestedPath);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Success<T> {
        private String status;
        private Integer code;
        private String message;
        private T result;

        private Success(ApiResponseStatus status, T result) {
            this.status = status.name();
            this.code = status.getCode();
            this.message = status.getMessage();
            this.result = result;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Error {
        private String status;
        private Integer code;
        private String message;
        private String requestPath;

        private Error(String status, Integer code, String message, String requestPath) {
            this.status = status;
            this.code = code;
            this.message = message;
            this.requestPath = requestPath;
        }
    }


}
