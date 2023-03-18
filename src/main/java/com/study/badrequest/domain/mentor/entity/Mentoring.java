package com.study.badrequest.domain.mentor.entity;

import com.study.badrequest.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MENTORING_ID")
public class Mentoring {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENTORING_ID")
    private Long id;
    @ManyToOne
    private Mentor mentor;
    @ManyToOne
    private Member mentee;

    public Mentoring(Mentor mentor, Member mentee) {
        this.mentor = mentor;
        this.mentee = mentee;
    }
}
