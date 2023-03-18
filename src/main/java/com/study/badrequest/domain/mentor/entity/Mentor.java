package com.study.badrequest.domain.mentor.entity;

import com.study.badrequest.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MENTOR")
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENTOR_ID")
    private Long id;
    private String title;
    private String introduce;
    @Enumerated(EnumType.STRING)
    private Job job;
    @Enumerated(EnumType.STRING)
    private Career career;
    private String currentCompany;
    private RegistrationStatus registrationStatus;
    private Integer starCount;
    private String starAverage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    private LocalDateTime registrationAt;
    private LocalDateTime updatedAt;
    private LocalDateTime registrationCancelAt;

    @Builder(builderMethodName = "createMentor")
    public Mentor(String title, String introduce, Job job, Career career, String currentCompany, Member member) {
        this.title = title;
        this.introduce = introduce;
        this.job = job;
        this.career = career;
        this.currentCompany = currentCompany;
        this.registrationStatus = RegistrationStatus.APPLIED;
        this.starCount = 0;
        this.member = member;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public void updateJob(Job job) {
        this.job = job;
    }

    public void updateCurrentCompany(String company) {
        this.currentCompany = company;
    }

    public void updateCareer(Career career) {
        this.career = career;
    }

    public void cancelRegistration(){
        this.registrationStatus = RegistrationStatus.CANCEL_REGISTRATION;
    }

}
