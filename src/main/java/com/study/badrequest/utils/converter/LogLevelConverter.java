package com.study.badrequest.utils.converter;


import com.study.badrequest.domain.log.LogLevel;
import org.springframework.core.convert.converter.Converter;

public class LogLevelConverter implements Converter<String, LogLevel> {

    @Override
    public LogLevel convert(String source) {
        return LogLevel.convert(source);
    }
}
