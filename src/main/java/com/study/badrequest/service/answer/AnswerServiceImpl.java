package com.study.badrequest.service.answer;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Answer;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.repository.answer.AnswerRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class AnswerServiceImpl {
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    public void createAnswer(Long memberId,Long questionId,String contents) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("회원을 찾을수 업습니다. 아이디= "+memberId));

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(""));

        Answer answer = Answer.createAnswer()
                .contents(contents)
                .question(question)
                .member(member)
                .build();

        answerRepository.save(answer);
    }
}
