package com.study.badrequest.domain.blog;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "BLOG", indexes = {
        @Index(name = "BLOG_LOCATION_IDX", columnList = "LOCATION", unique = true)
})
@EqualsAndHashCode(of = "id")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOG_ID")
    private Long id;
    @Column(name = "LOCATION", unique = true)
    private String location;
    private String title;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    private Boolean commentNotification;
    private Boolean emailNotification;
    private ExposureStatus exposureStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected Blog(String title, Member member) {
        this.location = generateRandomLocation();
        this.title = title;
        this.member = member;
        this.commentNotification = false;
        this.emailNotification = false;
        this.exposureStatus = ExposureStatus.PUBLIC;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = LocalDateTime.now();
    }

    public static Blog createBlog(Member member) {
        String title = member
                .getMemberProfile()
                .getNickname()
                .toLowerCase() + ".log";

        return new Blog(title, member);
    }

    public void changeCommentNotification(Boolean commentNotification){
        this.commentNotification = commentNotification;
        this.updatedAt = LocalDateTime.now();
    }
    public void changeEmailNotification(Boolean emailNotification){
        this.emailNotification = emailNotification;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeExposureToDelete() {
        this.exposureStatus = ExposureStatus.DELETE;
        this.deletedAt = LocalDateTime.now();
    }

    private String generateRandomLocation() {

        char[] uuidArray = UUID.randomUUID().toString().toLowerCase().toCharArray();
        String prefix = "@";
        StringBuilder locationBuilder = new StringBuilder();

        Random random = new Random();
        int length = uuidArray.length;

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(length);
            locationBuilder.append(uuidArray[randomIndex]);
        }
        String suffix = locationBuilder.toString();

        return prefix + suffix;
    }

    public void replaceLocationToRandom() {
        this.location = generateRandomLocation();
    }

}
