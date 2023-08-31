package com.study.badrequest.question.command.domain.dto;

import com.study.badrequest.question.command.domain.values.WriterType;

public record RegisterWriter(Long memberId, String nickname, String profileImage, Integer activeScore,
                             WriterType writerType) {
}
