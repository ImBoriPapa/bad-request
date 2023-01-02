package com.study.badrequest.commons.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.BasicException;
import com.study.badrequest.commons.exception.JwtAuthenticationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@RequiredArgsConstructor
public class ResponseForm {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Of<T> {
        private CustomStatus status;
        private int code;
        private String message;
        private T result;

        public Of(CustomStatus status, T result) {
            this.status = status;
            this.code = status.getCode();
            this.message = status.getMessage();
            this.result = result;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Error {
        private CustomStatus status;
        private String requestPath;
        private int errorCode;
        private List<String> message;

        public Error(Exception ex, String request) {
            this.status = CustomStatus.ERROR;
            this.requestPath = request;
            this.errorCode = CustomStatus.ERROR.getCode();
            this.message = List.of(CustomStatus.ERROR.getMessage());
        }

        public Error(BasicException ex, String request) {
            this.status = ex.getStatus();
            this.requestPath = request;
            this.errorCode = ex.getErrorCode();
            this.message = ex.getErrorMessage();
        }
    }


}
