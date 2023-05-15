package com.study.badrequest.utils.hash_tag;

public class HashTagUtils {
    public static String stringToHashTagString(String string) {
        return "#"+string.toLowerCase().trim().replace(" ","-");
    }

    public static String hashTagToTag(String hashTag) {
        return hashTag.replace("#", "");
    }

}
