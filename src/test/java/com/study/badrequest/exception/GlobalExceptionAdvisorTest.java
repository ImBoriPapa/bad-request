package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.commons.response.ApiResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionAdvisorTest {

    private final GlobalExceptionAdvisor globalExceptionAdvisor;

    public GlobalExceptionAdvisorTest() {
        this.globalExceptionAdvisor = new GlobalExceptionAdvisor();
    }


    @Test
    @DisplayName("test")
    void test() throws Exception {
        //given
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/api/v2/posts");
        Mockito.when(request.getMethod()).thenReturn("PATCH");
        GlobalExceptionAdvisor globalExceptionAdvisor = new GlobalExceptionAdvisor();
        Exception exception = new Exception("test");
        //when
        ResponseEntity<ApiResponse.Error> responseEntity = globalExceptionAdvisor.handleException(request, exception);
        //then
    }

    @Test
    @DisplayName("사용자 정의 예외 핸들링 테스트: 접근 제한 예외 발생")
    void customRuntimeExceptionTest() throws Exception {
        //given
        CustomRuntimeException customRuntimeException = CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.PERMISSION_DENIED);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/api/v2/posts");
        Mockito.when(request.getMethod()).thenReturn("PATCH");
        //when
        ResponseEntity<ApiResponse.Error> responseEntity = globalExceptionAdvisor.handleCustomRuntimeException(request, customRuntimeException);
        //then
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

    }

}