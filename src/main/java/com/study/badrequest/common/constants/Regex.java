package com.study.badrequest.common.constants;



public class Regex {
    //숫자
    public static final String NUMBER = "^[0-9]*$";
    //영어 +숫자
    public static final String ENG_NUMBER = "^[a-zA-Z0-9]*$";
    //한글 + 영문자만 가능합니다.
    public static final String KOR_ENG = "^[ㄱ-ㅎ|가-힣|a-zA-Z]*$";
    //한글 + 영문자 + 숫자만 가능합니다.
    public static final String KOR_ENG_NUMBER = "^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$";
    //비밀번호는 숫자,문자,특수문자 포함 8~15자리
    public static final String PASSWORD = "^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$";


}
