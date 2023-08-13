package com.study.badrequest.question.command.domain;

import com.study.badrequest.hashtag.command.domain.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question_event")
@Getter
public class QuestionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questionId;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Tag> tags = new ArrayList<>();

    public QuestionEvent(Long questionId) {
        this.questionId = questionId;
    }

    public void addHashTag(Tag tag) {
        this.tags.add(tag);
    }

    public static QuestionEvent createQuestionEvent(Long questionId, List<Tag> tags) {
        QuestionEvent questionEvent = new QuestionEvent(questionId);

        for (Tag tag : tags) {
            questionEvent.addHashTag(tag);
        }

        return questionEvent;
    }
}
