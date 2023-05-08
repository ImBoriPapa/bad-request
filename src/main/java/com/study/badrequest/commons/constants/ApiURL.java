package com.study.badrequest.commons.constants;

public class ApiURL {

    public static final String BASE_API_VERSION_URL = "/api/v2";
    //members
    public static final String POST_MEMBER_URL = BASE_API_VERSION_URL + "/members";
    public static final String POST_MEMBER_SEND_EMAIL_AUTHENTICATION_CODE = BASE_API_VERSION_URL + "/members/email-authentication";
    public static final String PATCH_MEMBER_PASSWORD_URL = BASE_API_VERSION_URL + "/members/{memberId}/password";
    public static final String PATCH_MEMBER_CONTACT_URL = BASE_API_VERSION_URL + "/members/{memberId}/contact";
    public static final String DELETE_MEMBER_URL = BASE_API_VERSION_URL + "/members/{memberId}";
    public static final String GET_MEMBER_DETAIL_URL = BASE_API_VERSION_URL + "/members/{memberId}";
    public static final String GET_MEMBER_PROFILE = BASE_API_VERSION_URL + "/members/{memberId}/profile";
    public static final String POST_MEMBER_TEMPORARY_PASSWORD_ISSUE_URL = BASE_API_VERSION_URL + "/members/temporary-password";
    public static final String PATCH_MEMBER_NICKNAME = BASE_API_VERSION_URL + "/members/{memberId}/nickname";
    public static final String PATCH_MEMBER_INTRODUCE = BASE_API_VERSION_URL + "/members/{memberId}/introduce";
    public static final String PATCH_MEMBER_PROFILE_IMAGE = BASE_API_VERSION_URL + "/members/{memberId}/profile-image";
    public static final String PATCH_MEMBER_PROFILE_IMAGE_TO_DEFAULT = BASE_API_VERSION_URL + "/members/{memberId}/default-image";
    public static final String GET_LOGGED_IN_MEMBER_INFORMATION = BASE_API_VERSION_URL+"/members/{memberId}/loggedInMember";
    //login
    public static final String LOGIN_URL = BASE_API_VERSION_URL + "/login";
    public static final String OAUTH2_LOGIN_URL = BASE_API_VERSION_URL + "/oauth/authorization";
    public static final String OAUTH2_REDIRECT_URL = BASE_API_VERSION_URL + "/oauth/client/*";
    public static final String OAUTH2_LOGIN_SUCCESS_URL = BASE_API_VERSION_URL + "/oauth/login/success";
    public static final String OAUTH2_LOGIN_FAILURE_URL = BASE_API_VERSION_URL + "/oauth/login/failure";
    public static final String OAUTH2_LOGIN_FAILURE_WITH_CODE = OAUTH2_LOGIN_FAILURE_URL + "?code=";
    public static final String LOGOUT_URL = BASE_API_VERSION_URL + "/logout";
    public static final String REFRESH_TOKEN_URL = BASE_API_VERSION_URL + "/refresh";
    //Question
    public static final String QUESTION_DETAIL_URL = BASE_API_VERSION_URL + "/questions/{questionId}";
    public static final String QUESTION_LIST_URL = BASE_API_VERSION_URL + "/questions";

    //Board
    public static final String BOARD_LIST_URL = BASE_API_VERSION_URL + "/board";
    public static final String BOARD_DETAIL_URL = BASE_API_VERSION_URL + "/board/{boardId}";
}
