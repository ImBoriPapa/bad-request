package com.study.badrequest.utils.email;

import com.study.badrequest.common.exception.CustomRuntimeException;

import static com.study.badrequest.common.response.ApiResponseStatus.EMAIL_MUST_NOT_BE_NULL;
import static com.study.badrequest.common.response.ApiResponseStatus.INVALID_EMAIL_FORM;

public class EmailFormatter {

    public static String convertDomainToLowercase(String email) {

        if (email == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(EMAIL_MUST_NOT_BE_NULL);
        }

        final int index = getIndex(email);

        return getLocalPart(email, index) + "@" + getDomainPart(email, index);

    }

    private static String getDomainPart(String email, int index) {
        final String domain = email.substring(index + 1);

        if (domain.length() == 0) {
            throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
        }

        if (!domain.contains(".")) {
            throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
        }

        int domainIndex = domain.lastIndexOf(".");

        final String domainPrefix = domain.substring(0, domainIndex).trim();

        if (domainPrefix.length() == 0) {
            throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
        }

        final String suffix = domain.substring(domainIndex + 1).trim();

        if (suffix.length() == 0) {
            throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
        }

        return domain.toLowerCase().trim();
    }

    private static String getLocalPart(String email, int index) {
        final String local = email.substring(0, index).trim();

        if (local.length() == 0) {
            throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
        }

        return local.trim();
    }

    private static int getIndex(String email) {
        if (!email.contains("@")) {
            throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
        }

        return email.lastIndexOf('@');
    }


}
