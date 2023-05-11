package com.study.badrequest.commons.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum ApiResponseStatus {
    // TODO: 2023/04/18 정리
    SUCCESS(1000, "요청에 성공했습니다.", HttpStatus.OK),
    LOGOUT_SUCCESS(1001, "로그아웃 요청이 성공했습니다.", HttpStatus.OK),
    ERROR(1002, "서버에 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1003, "요청 값 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    PERMISSION_DENIED(1004, "접근 권한이 없습니다.", HttpStatus.BAD_REQUEST),
    /**
     * 파일 업로드 관련
     * code: 1100~
     */
    WRONG_FILE_ERROR(1101, "잘못된 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    UPLOAD_FAIL_ERROR(1102, "파일 업로드에 실패했습니다.", HttpStatus.BAD_REQUEST),
    NOT_SUPPORT_ERROR(1103, "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    TOO_BIG_PROFILE_IMAGE_SIZE(1104, "프로필 이미지 파일의 크기는 500KB이하만 가능합니다.", HttpStatus.BAD_REQUEST),
    /**
     * 로그인 관련
     */
    IS_NOT_CONFIRMED_MAIL(1200, "이메일 인증 후 로그인 해주세요", HttpStatus.UNAUTHORIZED),
    IS_EXPIRED_TEMPORARY_PASSWORD(1201, "만료된 임시 비밀번호입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_MATCH(1500, "저장된 토큰과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_IS_EMPTY(1501, "AccessToken 이 없습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_IS_EXPIRED(1502, "토큰의 유효기간이 만료되었S습니다.", HttpStatus.BAD_REQUEST),
    TOKEN_IS_DENIED(1503, "잘못된 토큰입니다.", HttpStatus.BAD_REQUEST),
    TOKEN_IS_ERROR(1504, "문제가 있는 토큰입니다.", HttpStatus.BAD_REQUEST),
    REFRESH_COOKIE_IS_EMPTY(1505, "리프레시 토큰 쿠키를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    LOGIN_FAIL(1600, "로그인에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    ALREADY_LOGOUT(1601, "로그아웃된 계정입니다. 다시 로그인 해주세요.", HttpStatus.BAD_REQUEST),
    WRONG_ONE_TIME_CODE(1602, "잘못된 1회용 인증 코드입니다. 코드를 확인해 주세요", HttpStatus.UNAUTHORIZED),
    NOT_MATCH_ONE_TIME_CODE(1603, "발급된 인증코드와 다릅니다. 코드를 확인해 주세요", HttpStatus.UNAUTHORIZED),
    //OAUTH2
    ALREADY_REGISTERED_OAUTH2_EMAIL(1700, "이미 등록된 이메일입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_SELF_LOGIN_EMAIL(1701, "이미 자체 등록된 이메일입니다.", HttpStatus.BAD_REQUEST),
    /**
     * 회원 관련
     * code: 2000~
     */
    NOTFOUND_MEMBER(2000, "회원정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOTFOUND_AUTHORITY(2001, "권한정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    WRONG_AUTHORITY(2002, "잘못된 권한정보 입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(2003, "잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),
    WRONG_EMAIL_PATTERN(2004, "잘못된 이메일 형식입니다.", HttpStatus.BAD_REQUEST),
    WRONG_EMAIL_AUTHENTICATION_CODE(2005, "잘못된 이메일 인증 코드입니다.", HttpStatus.BAD_REQUEST),
    NOTFOUND_AUTHENTICATION_EMAIL(2006, "인증 메일 요청 정보를 찾을수 없습니다. 이메일 혹은 인증메일 만료 시간을 확인해 주세요", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT(2007, "새 비밀번호는 기존 비밀과 같을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER(2008, "요청된 회원과 로그인된 회원의 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL(2005, "이미 사용중인 이메일입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_CONTACT(2006, "이미 사용중인 연락처 입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PARAMETER(2007, "파라미터값을 확인해 주세요.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_CATEGORY(2008, "존재하지 않는 카테고리 입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_TOPIC(2009, "존재하지 않는 카테고리 입니다.", HttpStatus.BAD_REQUEST),
    /**
     * 질문
     */
    NOT_EXIST_SORT_VALUE(2500, "존재하지 않는 정렬 조건입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_QUESTION_STATUS_VALUE(2501, "존재하지 않는 질문 상태 입니다.", HttpStatus.BAD_REQUEST),

    /**
     * 게시판 관련
     * code: 3000~
     */
    NOT_FOUND_BOARD(3000, "존재하지 않는 게시판 입니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCH_BOARD_WRITER(3001, "게시판 수정은 작성자만 가능합니다.", HttpStatus.BAD_REQUEST),
    /**
     * 댓글 관련
     * code: 4000~
     */
    NOT_FOUND_COMMENT(4000, "존재하지 않는 댓글 입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND_SUB_COMMENT(4001, "존재하지 않는 대댓글 입니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ApiResponseStatus(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static ApiResponseStatus findCustomStatusByCode(Integer code) {
        return Arrays.stream(ApiResponseStatus.values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Undefined state"));
    }
}
