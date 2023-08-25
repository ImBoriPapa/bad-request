package com.study.badrequest.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ApiResponseStatus {

    SUCCESS(1000, "요청에 성공했습니다.", OK),
    LOGOUT_SUCCESS(1001, "로그아웃 요청이 성공했습니다.", OK),
    SERVER_ERROR(1002, "서버에 문제가 발생했습니다.", INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1003, "요청 값 검증에 실패했습니다.", BAD_REQUEST),
    PERMISSION_DENIED(1004, "접근 권한이 없습니다.", FORBIDDEN),
    BANNED_WORD(1005, "사용 금지된 단어를 사용하였습니다.", BAD_REQUEST),
    EMAIL_MUST_NOT_BE_NULL(1006, "이메일은 NULL 을 허용하지 않습니다.", BAD_REQUEST),
    INVALID_EMAIL_FORM(1007, "잘못된 이메일 형식입니다.", BAD_REQUEST),
    FOUND_ACTIVE_MEMBERS_WITH_DUPLICATE_EMAILS(1008, "중복된 이메일로 활동중인 회원을 발견했습니다.", BAD_REQUEST),
    PASSWORD_MUST_NOT_BE_NULL(1008, "패스워드는 NULL 을 허용하지 않습니다.", BAD_REQUEST),

    /**
     * 파일 업로드 관련
     * code: 1100~
     */
    WRONG_FILE_ERROR(1101, "잘못된 파일 형식입니다.", BAD_REQUEST),
    UPLOAD_FAIL_ERROR(1102, "파일 업로드에 실패했습니다.", BAD_REQUEST),
    NOT_SUPPORT_ERROR(1103, "지원하지 않는 파일 형식입니다.", BAD_REQUEST),
    TOO_BIG_PROFILE_IMAGE_SIZE(1104, "프로필 이미지 파일의 크기는 500KB이하만 가능합니다.", BAD_REQUEST),
    NOT_FOUND_IMAGE_FILE(1105, "이미지 파일을 찾을수 없습니다.", BAD_REQUEST),
    /**
     * 로그인 관련
     */
    IS_NOT_CONFIRMED_MAIL(1200, "이메일 인증 후 로그인 해주세요", UNAUTHORIZED),
    IS_EXPIRED_TEMPORARY_PASSWORD(1201, "만료된 임시 비밀번호입니다.", UNAUTHORIZED),
    TOKEN_NOT_MATCH(1203, "저장된 토큰과 일치하지 않습니다.", BAD_REQUEST),
    ACCESS_TOKEN_IS_EMPTY(1204, "Access Token 이 없습니다.", BAD_REQUEST),
    REFRESH_TOKEN_IS_EMPTY(1205, "Refresh Token 이 없습니다.", BAD_REQUEST),
    ACCESS_TOKEN_IS_EXPIRED(1206, "Access Token의 유효기간이 만료되었습니다.", UNAUTHORIZED),
    REFRESH_TOKEN_IS_EXPIRED(1207, "Refresh Token의 유효기간이 만료되었습니다.", UNAUTHORIZED),
    ACCESS_TOKEN_IS_DENIED(1208, "잘못된 Access Token입니다.", BAD_REQUEST),
    REFRESH_TOKEN_IS_DENIED(1209, "잘못된 Refresh Token입니다.", BAD_REQUEST),
    ACCESS_TOKEN_IS_ERROR(1210, "문제가 있는 Access Token 입니다.", BAD_REQUEST),
    REFRESH_TOKEN_IS_ERROR(1211, "문제가 있는 Refresh Token 입니다.", BAD_REQUEST),
    REFRESH_COOKIE_IS_EMPTY(1212, "리프레시 토큰 쿠키를 찾을 수 없습니다.", BAD_REQUEST),
    LOGIN_FAIL(1300, "로그인에 실패했습니다.", UNAUTHORIZED),
    ALREADY_LOGOUT(1301, "로그아웃된 계정입니다. 다시 로그인 해주세요.", BAD_REQUEST),
    WRONG_ONE_TIME_CODE(1302, "잘못된 1회용 인증 코드입니다. 코드를 확인해 주세요", UNAUTHORIZED),
    CAN_NOT_FIND_MEMBER_BY_DISPOSABLE_AUTHENTICATION_CODE(1303, "발급된 인증코드로 회원정보를 찾을 수 없습니다. 코드를 확인해 주세요", UNAUTHORIZED),
    EMPTY_ONE_TIME_CODE(1304, "발급된 인증코드와 필요합니다.. 코드를 확인해 주세요", UNAUTHORIZED),
    //OAUTH2
    ALREADY_REGISTERED_BY_OAUTH2(1305, "요청 하신 이메일은 이미 등록된 이메일입니다.", BAD_REQUEST),
    ALREADY_REGISTERED_BY_EMAIL(1306, "요청 하신 이메일은 이미 등록된 이메일입니다.", BAD_REQUEST),
    IS_WITHDRAWN_MEMBER(1307, "탈퇴한 회원입니다.", BAD_REQUEST),
    THIS_IS_NOT_REGISTERED_AS_MEMBER(1308, "회원 가입되지 않은 이메일입니다.", BAD_REQUEST),
    /**
     * 회원 관련
     * code: 2000~
     */
    NOTFOUND_MEMBER(2000, "회원정보를 찾을 수 없습니다.", BAD_REQUEST),
    NOTFOUND_AUTHORITY(2001, "권한정보를 찾을 수 없습니다.", BAD_REQUEST),
    WRONG_AUTHORITY(2002, "잘못된 권한정보 입니다.", BAD_REQUEST),
    WRONG_PASSWORD(2003, "잘못된 비밀번호입니다.", BAD_REQUEST),
    WRONG_EMAIL_PATTERN(2004, "잘못된 이메일 형식입니다.", BAD_REQUEST),
    WRONG_EMAIL_AUTHENTICATION_CODE(2005, "잘못된 이메일 인증 코드입니다.", BAD_REQUEST),
    NOTFOUND_AUTHENTICATION_EMAIL(2006, "인증 메일 요청 정보를 찾을수 없습니다. 이메일 혹은 인증메일 만료 시간을 확인해 주세요", BAD_REQUEST),
    NEW_PASSWORD_CANNOT_BE_SAME_AS_CURRENT(2007, "새 비밀번호는 기존 비밀과 같을 수 없습니다.", BAD_REQUEST),
    NOT_MATCH_REQUEST_MEMBER_WITH_LOGGED_IN_MEMBER(2008, "요청된 회원과 로그인된 회원의 정보가 일치하지 않습니다.", BAD_REQUEST),
    DUPLICATE_EMAIL(2009, "이미 사용중인 이메일입니다.", BAD_REQUEST),
    DUPLICATE_CONTACT(2010, "이미 사용중인 연락처 입니다.", BAD_REQUEST),
    CONTACT_MUST_NOT_BE_NULL(2011, "연락처는 NULL 을 하용하지 않습니다.", BAD_REQUEST),
    NOT_FOUND_TEMPORARY_PASSWORD(2012, "임시비밀번호 정보를 찾을 수 없습니다.", UNAUTHORIZED),
    FAIL_GET_OAUTH2_USER_INFO(2013, "Oauth 유저 정보를 받아오는것에 실패했습니다.", UNAUTHORIZED),
    /**
     * 질문
     */
    NOT_EXIST_SORT_VALUE(2500, "존재하지 않는 정렬 조건입니다.", BAD_REQUEST),
    NOT_EXIST_QUESTION_STATUS_VALUE(2501, "존재하지 않는 질문 상태 입니다.", BAD_REQUEST),
    NOT_FOUND_QUESTION(2505, "질문을 찾을 수 없습니다.", BAD_REQUEST),
    AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED(2530, "태그는 최소 한개 이상 사용해야합니디.", BAD_REQUEST),
    CAN_NOT_DELETE_DEFAULT_IMAGE(2600, "기본 이미지는 삭제할 수 없습니다.", BAD_REQUEST),
    NOT_FOUND_WRITER(2510, "작성자 정보를 찾을 수 없습니다.", BAD_REQUEST),

    /**
     * 답변
     */
    NOT_FOUND_ANSWER(2550, "해당 답변을 찾을 수 없습니다.", BAD_REQUEST),
    NOT_ALLOW_EMPTY_ANSWER(2551, "답변 내용은 공백을 허용하지 않습니다.", BAD_REQUEST),
    NOT_ALLOW_MODIFY_ANSWER(2552, "회원님이 작성하신 답변만 수정 할수 있습니다.", FORBIDDEN),
    NOT_ALLOW_DELETE_ANSWER(2553, "회원님이 작성하신 답변만 삭제 할수 있습니다.", FORBIDDEN),

    /**
     * 태그
     */
    NOT_FOUND_QUESTION_TAG(2600, "해당 질문에 태그를 찾을 수 없습니다.", BAD_REQUEST),
    /**
     * 게시판 관련
     * code: 3000~
     */
    NOT_FOUND_BOARD(3000, "존재하지 않는 게시판 입니다.", BAD_REQUEST),
    NOT_MATCH_BOARD_WRITER(3001, "게시판 수정은 작성자만 가능합니다.", BAD_REQUEST),
    /**
     * 댓글 관련
     * code: 4000~
     */
    NOT_FOUND_COMMENT(4000, "존재하지 않는 댓글 입니다.", BAD_REQUEST),
    NOT_FOUND_SUB_COMMENT(4001, "존재하지 않는 대댓글 입니다.", BAD_REQUEST),

    /**
     * 메일
     */
    FAIL_SEND_MAIL(5000, "메일 발송에 실패했습니다.", INTERNAL_SERVER_ERROR);
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
