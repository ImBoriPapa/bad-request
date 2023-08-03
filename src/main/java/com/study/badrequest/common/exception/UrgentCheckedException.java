package com.study.badrequest.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrgentCheckedException extends RuntimeException {
    private String message;
}
