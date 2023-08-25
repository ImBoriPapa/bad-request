package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.MemberId;
import com.study.badrequest.question.command.domain.Writer;
import com.study.badrequest.question.command.domain.WriterType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WriterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long memberId;
    private String nickname;
    private String profileImage;
    private Integer activityScore;
    @Enumerated(EnumType.STRING)
    private WriterType writerType;

    @Builder(access = AccessLevel.PRIVATE)
    private WriterEntity(Long id, Long memberId, String nickname, String profileImage, Integer activityScore, WriterType writerType) {
        this.id = id;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
        this.writerType = writerType;
    }

    public static WriterEntity fromModel(Writer writer) {
        return WriterEntity.builder()
                .id(writer.getId())
                .memberId(writer.getMemberId().getId())
                .nickname(writer.getNickname())
                .profileImage(writer.getProfileImage())
                .activityScore(writer.getActivityScore())
                .writerType(writer.getWriterType())
                .build();
    }

    public Writer toModel() {
        return Writer.initialize(getId(), new MemberId(getMemberId()), getNickname(), getProfileImage(), getActivityScore(), getWriterType());
    }
}
