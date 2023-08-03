package com.study.badrequest.utils.email;

import com.study.badrequest.common.exception.CustomRuntimeException;

import static com.study.badrequest.common.response.ApiResponseStatus.INVALID_EMAIL_FORM;

public class EmailUtils {

    public static String convertDomainToLowercase(String email) {
        int index = email.lastIndexOf('@');

        if (index != -1) {
            String localPart = email.substring(0, index);
            String domainPart = email.substring(index + 1);
            String convertedDomainPart = domainPart.toLowerCase();
            return localPart + "@" + convertedDomainPart;
        }

        throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
    }
}
