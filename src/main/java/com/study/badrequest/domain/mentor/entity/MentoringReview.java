package com.study.badrequest.domain.mentor.entity;

import com.study.badrequest.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentoringReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String review;
    private MentoringStar star;
    @OneToOne
    private Member mentee;
    @ManyToOne
    @JoinColumn
    private Mentor mentor;
}
