package com.study.badrequest.testHelper;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.hashTag.HashTag;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionMetrics;
import com.study.badrequest.domain.question.QuestionTag;
import com.study.badrequest.repository.hashTag.HashTagRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
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

    public void restartAutoIncrement() {
        String query1 = "ALTER TABLE MEMBER ALTER COLUMN MEMBER_ID RESTART WITH 1";
        String query2 = "ALTER TABLE QUESTION ALTER COLUMN QUESTION_ID RESTART WITH 1";
        entityManager.createNativeQuery(query1).executeUpdate();
        entityManager.createNativeQuery(query2).executeUpdate();
    }

    public List<Member> createSampleMembers() {
        List<Member> memberList = new ArrayList<>();
        IntStream.rangeClosed(1, 10).forEach(index -> {
                    Member member = Member.createSelfRegisteredMember(
                            "sample" + index + "@gmail.com",
                            passwordEncoder.encode("sample1234!@"),
                            "010" + new Random().nextInt(10000000) + 1,
                            new MemberProfile("샘플유저", ProfileImage.createDefault("image")));
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
                    Question question = Question.createQuestion()
                            .title("제목")
                            .contents("내용")
                            .member(sampleMembers.get(randomMemberId))
                            .build();
                    question.addQuestionMetrics(QuestionMetrics.createQuestionMetrics());
                    list.add(question);

                    HashTag hashTag1 = hashTagRepository.save(new HashTag("#tag" + new Random().nextInt(10) + 1));
                    HashTag hashTag2 = hashTagRepository.save(new HashTag("#tag" + new Random().nextInt(10) + 1));

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
}