package com.study.badrequest.commons.mapper;

import com.study.badrequest.commons.form.ResponseForm;
import org.springframework.cglib.core.internal.Function;

public class ResponseMapper<T> implements Function<T, ResponseForm> {

    @Override
    public ResponseForm apply(T t) {
        return null;
    }
}
