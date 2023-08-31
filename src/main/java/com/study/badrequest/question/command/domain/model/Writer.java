package com.study.badrequest.question.command.domain.model;

import com.study.badrequest.question.command.domain.dto.RegisterWriter;
import com.study.badrequest.question.command.domain.values.MemberId;
import com.study.badrequest.question.command.domain.values.WriterType;
import lombok.*;

@Getter
public final class Writer {
    private final Long id;
    private final MemberId memberId;
    private final String nickname;
    private final String profileImage;
    private final Integer activityScore;
    private final WriterType writerType;

    @Builder(access = AccessLevel.PROTECTED)
    public Writer(Long id, MemberId memberId, String nickname, String profileImage, Integer activityScore, WriterType writerType) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
        this.writerType = writerType;
    }

    public static Writer registerWriter(RegisterWriter registerWriter) {
        return Writer.builder()
                .memberId(new MemberId(registerWriter.memberId()))
                .nickname(registerWriter.nickname())
                .profileImage(registerWriter.profileImage())
                .activityScore(registerWriter.activeScore())
                .writerType(registerWriter.writerType())
                .build();
    }

    public static Writer initialize(Long id, MemberId memberId, String nickname, String profileImage, Integer activityScore, WriterType writerType) {
        return Writer.builder()
                .id(id)
                .memberId(memberId)
                .nickname(nickname)
                .profileImage(profileImage)
                .activityScore(activityScore)
                .writerType(writerType)
                .build();
    }
}
