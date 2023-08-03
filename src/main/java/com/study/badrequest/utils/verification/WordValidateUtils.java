package com.study.badrequest.utils.verification;


import com.study.badrequest.common.exception.CustomRuntimeException;

import java.util.List;

import static com.study.badrequest.common.response.ApiResponseStatus.BANNED_WORD;

public class WordValidateUtils {

    public static void findBannedWord(String word) {

        List<String> bannedWords = List.of("시팔", "십팔", "fucking", "좆같");

        for (String bannedWord : bannedWords) {
            if (bannedWord.equalsIgnoreCase(word)) {
                throw CustomRuntimeException.createWithApiResponseStatus(BANNED_WORD);
            }
        }


    }

}
