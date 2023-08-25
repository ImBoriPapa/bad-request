package com.study.badrequest.question.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.question.command.domain.*;
import com.study.badrequest.question.command.domain.dto.CreateQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuestionServiceImpl {
    private final WriterRepository writerRepository;
    private final MemberInformationRepository memberInformationRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;

    @Transactional
    public Long createQuestion(Long memberId, String title, String contents, List<Long> tagIds) {
        //회원 아이디로 작성자 조회
        Optional<Writer> writerOptional = writerRepository.findByMemberId(memberId);

        //작성자 정보가 없으면 회원 정보 조회후 작성자 등록
        final Writer writer = writerOptional.orElseGet(() -> getNewWriter(memberId));
        //태그 조회
        List<Tag> tags = tagRepository.findAllByNameIn(tagIds);
        //질문 생성
        Question question = Question.createQuestion(new CreateQuestion(title, contents), writer);
        //질문 저장
        Question save = questionRepository.save(question);
        //질문 태그 연결 객체 생성
        List<QuestionTag> questionTags = new ArrayList<>();

        for (Tag tag : tags) {
            QuestionTag questionTag = QuestionTag.createQuestionTag(save, tag);
            questionTags.add(questionTag);
        }
        //질문 태그 저장
        questionTagRepository.saveAllQuestionTag(questionTags);

        return save.getId();
    }

    private Writer getNewWriter(Long memberId) {

        MemberInformation memberInformation = getMemberInformation(memberId);

        RegisterWriter registerWriter = createRegisterWriterByMemberInformation(memberInformation);

        return writerRepository.save(Writer.registerWriter(registerWriter));
    }

    private static RegisterWriter createRegisterWriterByMemberInformation(MemberInformation memberInformation) {
        return new RegisterWriter(
                memberInformation.getMemberId(),
                memberInformation.getNickname(),
                memberInformation.getProfileImage(),
                memberInformation.getActivityScore(),
                WriterType.MEMBER);
    }

    private MemberInformation getMemberInformation(Long memberId) {
        return memberInformationRepository.findByMemberId(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
    }

}
