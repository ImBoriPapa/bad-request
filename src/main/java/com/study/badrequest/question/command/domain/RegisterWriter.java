package com.study.badrequest.question.command.domain;

public record RegisterWriter(Long memberId,String nickname,String profileImage,Integer activeScore,WriterType writerType) {
}
