package com.study.badrequest.question.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.question.command.application.dto.CreateQuestionRequest;
import com.study.badrequest.question.command.domain.dto.CreateQuestion;
import com.study.badrequest.question.command.domain.dto.MemberInformation;
import com.study.badrequest.question.command.domain.dto.RegisterWriter;
import com.study.badrequest.question.command.domain.model.*;
import com.study.badrequest.question.command.domain.repository.*;
import com.study.badrequest.question.command.domain.values.WriterType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class QuestionCreateServiceImpl implements QuestionCreateService{
    private final WriterRepository writerRepository;
    private final MemberInformationRepository memberInformationRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final QuestionTagRepository questionTagRepository;
    private final CountOfRecommendRepository countOfRecommendRepository;
    private final CountOfUnRecommendRepository countOfUnRecommendRepository;
    private final CountOfViewRepository countOfViewRepository;
    private final CountOfAnswerRepository countOfAnswerRepository;
    private final AttachedImageRepository attachedImageRepository;

    @Override
    @Transactional
    public Long createQuestion(CreateQuestionRequest request) {
        //질문 저장
        Question question = questionRepository.save(Question.createQuestion(createForm(request.memberId(), request.title(), request.contents())));
        //태그 저장
        saveQuestionTags(request.tagIds(), question);
        //이미지 저장
        saveImages(request.imageIds(), question);

        return question.getId();
    }

    private void saveImages(List<Long> images, Question question) {
        //임시 저장된 이미지 영구 저장으로 변경
        if (!images.isEmpty()) {
            List<AttachedImage> attachedImages = attachedImageRepository.findAllByIdIn(images);
            attachedImageRepository.saveAll(attachedImages.stream()
                    .map(attachedImage -> attachedImage.tempToSave(question))
                    .toList());
        }
    }

    private void saveQuestionTags(List<Long> tagIds, Question question) {
        //태그 조회
        final List<Tag> tags = tagRepository.findAllByNameIn(tagIds);

        //질문 태그 연결 객체 생성
        List<QuestionTag> questionTags = new ArrayList<>();

        for (Tag tag : tags) {
            QuestionTag questionTag = QuestionTag.createQuestionTag(question, tag);
            questionTags.add(questionTag);
        }
        //질문 태그 저장
        questionTagRepository.saveAllQuestionTag(questionTags);
    }

    private CreateQuestion createForm(Long memberId, String title, String contents) {
        final Writer writer = getWriter(memberId);
        //추천수 설정
        final CountOfRecommend countOfRecommend = getCountOfRecommend();
        //비추천 수 설정
        final CountOfUnRecommend countOfUnRecommend = getCountOfUnRecommend();
        //조회수 설정
        final CountOfView countOfView = getCountOfView();
        //답변수 설정
        final CountOfAnswer countOfAnswer = getCountOfAnswer();
        //CreateQuestion 생성
        return new CreateQuestion(writer, title, contents, countOfRecommend, countOfUnRecommend, countOfView, countOfAnswer);
    }

    private CountOfAnswer getCountOfAnswer() {
        return countOfAnswerRepository.findByCount(0L)
                .orElseGet(() -> countOfAnswerRepository.save(CountOfAnswer.createNewAnswerCount(0L)));
    }

    private CountOfView getCountOfView() {
        return countOfViewRepository.findByCount(0L)
                .orElseGet(() -> countOfViewRepository.save(CountOfView.createNewViewCountOfQuestion(0L)));
    }

    private CountOfUnRecommend getCountOfUnRecommend() {
        return countOfUnRecommendRepository.findByCount(0L)
                .orElseGet(() -> countOfUnRecommendRepository.save(CountOfUnRecommend.createNewUnRecommendCount(0L)));
    }

    private CountOfRecommend getCountOfRecommend() {
        return countOfRecommendRepository.findByCount(0L)
                .orElseGet(() -> countOfRecommendRepository.save(CountOfRecommend.createNewRecommendCount(0L)));
    }

    private Writer getWriter(Long memberId) {
        //회원 아이디로 작성자 조회, 저장된 작성자 정보가 없으면 회원 정보 조회후 작성자 등록
        return writerRepository.findByMemberId(memberId)
                .orElseGet(() -> getNewWriter(memberId));
    }

    private Writer getNewWriter(Long memberId) {

        MemberInformation memberInformation = getMemberInformation(memberId);

        RegisterWriter registerWriter = createRegisterWriterByMemberInformation(memberInformation);

        return writerRepository.save(Writer.registerWriter(registerWriter));
    }

    private RegisterWriter createRegisterWriterByMemberInformation(MemberInformation memberInformation) {
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
