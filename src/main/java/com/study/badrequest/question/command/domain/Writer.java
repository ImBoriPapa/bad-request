package com.study.badrequest.question.command.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "writer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class Writer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WRITER_ID")
    private Long id;
    private MemberId memberId;
    private String nickname;
    private String profileImage;
    private Integer activityScore;
    @Enumerated(EnumType.STRING)
    private WriterType writerType;

    @Builder(access = AccessLevel.PROTECTED)
    protected Writer(MemberId memberId, String nickname, String profileImage, Integer activityScore, WriterType writerType) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.activityScore = activityScore;
        this.writerType = writerType;
    }

    public static Writer createWriter(Long memberId, String nickname, String profileImage, Integer activityScore, WriterType writerType) {

        return Writer.builder()
                .memberId(new MemberId(memberId))
                .nickname(nickname)
                .profileImage(profileImage)
                .activityScore(activityScore)
                .writerType(writerType)
                .build();
    }
}
