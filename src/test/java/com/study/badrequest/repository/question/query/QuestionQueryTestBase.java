package com.study.badrequest.repository.question.query;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.hashTag.HashTag;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.memberProfile.MemberProfile;
import com.study.badrequest.domain.memberProfile.ProfileImage;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionMetrics;
import com.study.badrequest.domain.question.QuestionTag;
import com.study.badrequest.repository.hashTag.HashTagRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
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
        log.info("Create {} Sample Questions ",numberOfQuestions);
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
        return memberRepository.saveAllAndFlush(memberList);
    }

    private static Member createMemberWithIndex(int index) {
        Member member = Member.createWithEmail(
                "sample" + index + "@gmail.com",
                "sample1234!@",
                "010" + new Random().nextInt(10000000) + 1);
        member.assignMemberProfile(MemberProfile.createMemberProfile("nickname" + index, ProfileImage.createDefaultImage("image")));
        return member;
    }
}
