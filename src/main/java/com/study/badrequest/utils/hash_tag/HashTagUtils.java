package com.study.badrequest.utils.hash_tag;

import java.util.function.Function;

public class HashTagUtils {
    public static String stringToHashTag(String string) {
        return "#"+string.toLowerCase().trim().replace(" ","-");
    }

    public static String hashTagToTag(String hashTag) {
        return hashTag.replace("#", "");
    }

}
