package com.study.badrequest.testHelper;

import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.answer.command.domain.Answer;
import com.study.badrequest.answer.command.domain.AnswerRecommendation;
import com.study.badrequest.hashtag.command.domain.HashTag;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionMetrics;
import com.study.badrequest.question.command.domain.QuestionTag;
import com.study.badrequest.recommandation.command.domain.RecommendationKind;
import com.study.badrequest.answer.command.domain.AnswerRepository;
import com.study.badrequest.answer.command.domain.AnswerRecommendationRepository;
import com.study.badrequest.hashtag.command.domain.HashTagRepository;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.question.command.domain.QuestionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@Transactional
@RequiredArgsConstructor
public class TestData {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final QuestionTagRepository questionTagRepository;
    private final QuestionRepository questionRepository;
    private final HashTagRepository hashTagRepository;
    private final EntityManager entityManager;
    private final AnswerRepository answerRepository;
    private final AnswerRecommendationRepository answerRecommendationRepository;

    public void restartAutoIncrement() {
        String query1 = "ALTER TABLE MEMBER ALTER COLUMN MEMBER_ID RESTART WITH 1";
        String query2 = "ALTER TABLE QUESTION ALTER COLUMN QUESTION_ID RESTART WITH 1";
        String query3 = "ALTER TABLE ANSWER ALTER COLUMN ANSWER_ID RESTART WITH 1";
        entityManager.createNativeQuery(query1).executeUpdate();
        entityManager.createNativeQuery(query2).executeUpdate();
        entityManager.createNativeQuery(query3).executeUpdate();
    }

    public List<Member> createSampleMembers() {
        List<Member> memberList = new ArrayList<>();
        IntStream.rangeClosed(1, 10).forEach(index -> {
                    Member member = Member.createWithEmail(
                            "sample" + index + "@gmail.com",
                            passwordEncoder.encode("sample1234!@"),
                            "010" + new Random().nextInt(10000000) + 1);
                    memberList.add(member);
                }
        );
        return memberRepository.saveAllAndFlush(memberList);
    }

    public void createSampleQuestion() {
        List<Member> sampleMembers = createSampleMembers();
        List<Question> list = new ArrayList<>();

        IntStream.rangeClosed(1, 15).forEach(
                index -> {
                    int randomMemberId = new Random().nextInt(sampleMembers.size() - 1) + 1;

                    Question question = Question.createQuestion("제목", "내용", sampleMembers.get(randomMemberId), QuestionMetrics.createQuestionMetrics());

                    list.add(question);

                    HashTag hashTag1 = hashTagRepository.save(HashTag.createHashTag("#tag" + new Random().nextInt(10) + 1));
                    HashTag hashTag2 = hashTagRepository.save(HashTag.createHashTag("#tag" + new Random().nextInt(10) + 1));

                    QuestionTag questionTag1 = QuestionTag.createQuestionTag(question, hashTag1);
                    QuestionTag questionTag2 = QuestionTag.createQuestionTag(question, hashTag2);
                    questionTagRepository.saveAll(List.of(questionTag1, questionTag2));
                    if (index == 4) {
                        question.changeExposure(ExposureStatus.DELETE);
                    }

                }
        );
        questionRepository.saveAllAndFlush(list);
    }

    public void createSampleAnswer() {
        createSampleQuestion();
        List<Member> members = memberRepository.findAll();
        List<Question> questions = questionRepository.findAll();
        ArrayList<Answer> answerList = new ArrayList<>();
        questions.forEach(question -> {
            Answer answer1 = Answer.createAnswer()
                    .contents("답변 1 입니다.")
                    .question(question)
                    .member(members.get(new Random().nextInt(members.size() - 1) + 1))
                    .build();

            Answer answer2 = Answer.createAnswer()
                    .contents("답변 2 입니다.")
                    .question(question)
                    .member(members.get(new Random().nextInt(members.size() - 1) + 1))
                    .build();

            Answer answer3 = Answer.createAnswer()
                    .contents("답변 3 입니다.")
                    .question(question)
                    .member(members.get(new Random().nextInt(members.size() - 1) + 1))
                    .build();

            answerList.add(answer1);
            answerList.add(answer2);
            answerList.add(answer3);
        });

        List<Answer> answers = answerRepository.saveAllAndFlush(answerList);

        ArrayList<AnswerRecommendation> answerRecommendation = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerRecommendation recommendation = AnswerRecommendation.createRecommendation(members.get(new Random().nextInt(members.size() - 1) + 1), answer, RecommendationKind.RECOMMENDATION);
            answerRecommendation.add(recommendation);
        }

        answerRecommendationRepository.saveAllAndFlush(answerRecommendation);
    }
}
