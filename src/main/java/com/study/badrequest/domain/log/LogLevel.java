package com.study.badrequest.domain.log;

import lombok.Getter;

@Getter
public enum LogLevel {

    INFO("info"),
    ERROR("error");

    private String value;

    LogLevel(String value) {
        this.value = value;
    }

    public static LogLevel convert(String level) {
        if (level == null) {
            return null;
        }
        for (LogLevel l : LogLevel.values()) {

            if (l.value.equalsIgnoreCase(level)) {
                return l;
            }
        }
        throw new IllegalArgumentException("Log level이 잘못되었습니다.");

    }
}
