package com.study.badrequest.repository.question.query;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.hashtag.command.domain.HashTag;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberProfile;
import com.study.badrequest.member.command.domain.ProfileImage;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionMetrics;
import com.study.badrequest.question.command.domain.QuestionTag;
import com.study.badrequest.question.query.QuestionQueryRepositoryImpl;
import com.study.badrequest.hashtag.command.domain.HashTagRepository;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.question.command.domain.QuestionTagRepository;
import com.study.badrequest.testHelper.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class, QuestionQueryRepositoryImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class QuestionQueryTestBase {

    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected QuestionQueryRepositoryImpl questionQueryRepository;
    @Autowired
    protected HashTagRepository hashTagRepository;
    @Autowired
    protected QuestionTagRepository questionTagRepository;
    @Autowired
    protected QuestionRepository questionRepository;
    @Autowired
    protected EntityManager entityManager;
    @Autowired
    protected JPAQueryFactory jpaQueryFactory;

    protected void restartAutoIncrement() {
        log.info("Reset Auto Increment");
        String query1 = "ALTER TABLE MEMBER ALTER COLUMN MEMBER_ID RESTART WITH 1";
        String query2 = "ALTER TABLE QUESTION ALTER COLUMN QUESTION_ID RESTART WITH 1";

        entityManager.createNativeQuery(query1).executeUpdate();
        entityManager.createNativeQuery(query2).executeUpdate();

    }

    public void createSampleQuestion(int numberOfQuestions) {
        log.info("Create {} Sample Questions ", numberOfQuestions);
        List<Member> sampleMembers = createSampleMembers();
        List<Question> list = new ArrayList<>();

        IntStream.rangeClosed(1, numberOfQuestions).forEach(
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

    private List<Member> createSampleMembers() {
        List<Member> memberList = new ArrayList<>();
        IntStream.rangeClosed(1, 10).forEach(index -> memberList.add(createMemberWithIndex(index)));
//        memberRepository.saveAllAndFlushMembers(memberList);
        return null;
    }

    private static Member createMemberWithIndex(int index) {
        Member member = Member.createByEmail(
                "sample" + index + "@gmail.com",
                "sample1234!@",
                "010" + new Random().nextInt(10000000) + 1, MemberProfile.createMemberProfile("nickname" + index, ProfileImage.createDefaultImage("image")));
        ;
        return member;
    }
}
