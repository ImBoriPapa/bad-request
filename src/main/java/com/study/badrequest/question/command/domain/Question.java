package com.study.badrequest.question.command.domain;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.status.ExposureStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.common.status.ExposureStatus.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question")
@EqualsAndHashCode(of = "id")
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Writer writer;
    @Column(name = "title")
    private String title;
    @Column(name = "contents")
    @Lob
    private String contents;
    @Column(name = "preview")
    private String preview;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<QuestionTag> questionTags = new ArrayList<>();
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_metrics_id")
    private QuestionMetrics questionMetrics;
    @Column(name = "asked_at")
    private LocalDateTime askedAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Question(Writer writer, String title, String contents, LocalDateTime askedAt) {
        this.writer = writer;
        this.title = title;
        this.contents = contents;
        this.askedAt = askedAt;
    }

    /**
     * 질문 엔티티 생성
     *
     * @param title           질문 게시글 제목
     * @param contents        질문 내용 - Markdown
     * @param writer          질문 작성자
     * @param questionTags    질문 태그 - question (1:N) -> questionTag <-(N:1),tag 연결 테이블
     * @param questionMetrics 질문글의 관련 지표
     * @return Question
     * @ImplNote 질문글을 생성하면서 프리뷰를 만들고 질문 태그, 지표 데이터와 연관관계를 맺습니다.
     */
    public static Question createQuestion(String title, String contents, Writer writer, List<QuestionTag> questionTags, QuestionMetrics questionMetrics) {
        Question question = new Question(writer, title, contents, LocalDateTime.now());
        question.makePreview(contents);
        question.addQuestionMetrics(questionMetrics);

        for (QuestionTag questionTag : questionTags) {
            questionTag.assignQuestion(question);
        }

        return question;
    }

    /**
     * 질문 수정
     *
     * @param requesterId 회원식별아이디-memberId (Long)
     * @param title       (String)
     * @param contents    (String)
     */
    public void modifyTitleAndContents(Long requesterId, String title, String contents) {

        if (this.questionMetrics.getExposure() != DELETE) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION);
        }

        if (!this.writer.getMemberId().getId().equals(requesterId)) {
            throw CustomRuntimeException.createWithApiResponseStatus(PERMISSION_DENIED);
        }

        this.title = title;
        this.contents = contents;
        this.preview = makePreview(contents);
        this.modifiedAt = LocalDateTime.now();
    }

    public void updateWriter(String nickname, String profileImage, Integer activeScore) {
        this.writer.update(nickname, profileImage, activeScore);
    }

    private void addQuestionMetrics(QuestionMetrics questionMetrics) {
        this.questionMetrics = questionMetrics;
        questionMetrics.addQuestion(this);
    }

    private String makePreview(String contents) {
        return contents.length() > 50 ? contents.substring(0, 50) : contents;
    }

    public void incrementCountOfRecommendation() {
        this.questionMetrics.incrementCountOfRecommendations();
    }

    public void decrementCountOfRecommendation() {
        this.questionMetrics.decrementCountOfRecommendations();
    }
}
