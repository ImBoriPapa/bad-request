package com.study.badrequest.service.question;

import com.study.badrequest.domain.board.HashTag;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionTag;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.board.HashTagRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
import com.study.badrequest.utils.hash_tag.HashTagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionTagServiceImpl implements QuestionTagService {

    private final QuestionTagRepository questionTagRepository;
    private final HashTagRepository hashTagRepository;
    private final QuestionRepository questionRepository;
    @Transactional
    public void createQuestionTag(List<String> tags, Question question) {
        log.info("질문 태그 생성 시작 - QuestionId: {}, Requested Tag name: {}", question.getId(), tags.toArray());

        Set<String> requestedTags = tags.stream().map(HashTagUtils::stringToHashTagString).collect(Collectors.toSet());

        List<HashTag> findHashTagByRequestedTags = hashTagRepository.findAllByHashTagNameIn(requestedTags);

        // 기존에 저장된 해시태그가 없을 경우
        if (findHashTagByRequestedTags.isEmpty()) {
            saveQuestionTags(question, requestedTags);
        } else
            saveQuestionTags(question, requestedTags, findHashTagByRequestedTags);
    }

    private void saveQuestionTags(Question question, Set<String> requestedTags, List<HashTag> findHashTagByRequestedTags) {
        Map<String, QuestionTag> haveToSave = new HashMap<>();
        // 이미 등록된 해시태그로 질문 태그를 생성해서 haveToSave 에 저장
        findHashTagByRequestedTags.stream()
                .map(hashTag -> QuestionTag.createQuestionTag(question, hashTag))
                .forEach(questionTag -> haveToSave.put(questionTag.getHashTag().getHashTagName(), questionTag));

        // 이미 등록된 해시태그의 태그네임과 같지않은 요청된 태그네임만 새로운 해시태그로 저장
        List<HashTag> newHashTags = requestedTagMapToHashTags(requestedTags).stream()
                .filter(hashTag -> !haveToSave.containsKey(hashTag.getHashTagName()))
                .collect(Collectors.toList());

        //새롭게 저장된 해시태그를 haveToSave에 저장
        hashTagMapQuestionTag(question, newHashTags).forEach(questionTag -> haveToSave.put(questionTag.getHashTag().getHashTagName(), questionTag));

        //만들어진 모든 질문 태그를 저장
        questionTagRepository.saveAll(haveToSave.values());
    }

    private void saveQuestionTags(Question question, Set<String> requestedTags) {
        List<HashTag> newHashTags = requestedTagMapToHashTags(requestedTags);

        questionTagRepository.saveAll(hashTagMapQuestionTag(question, newHashTags));
    }

    private List<HashTag> requestedTagMapToHashTags(Set<String> requestedTags) {
        return requestedTags.stream()
                .map(HashTag::new)
                .collect(Collectors.toList());
    }

    private List<QuestionTag> hashTagMapQuestionTag(Question question, List<HashTag> newHashTags) {
        return hashTagRepository.saveAll(newHashTags)
                .stream().map(hashTag -> QuestionTag.createQuestionTag(question, hashTag))
                .collect(Collectors.toList());

    }

    @Transactional
    public void addQuestionTag(Long questionId, String questionTag) {
        log.info("질문 태그 추가 시작");
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));

        String hashTag = HashTagUtils.stringToHashTagString(questionTag);

        QuestionTag newQuestionTag = hashTagRepository
                .findByHashTagName(hashTag)
                .map(tag -> QuestionTag.createQuestionTag(question, tag))
                .orElseGet(() -> QuestionTag.createQuestionTag(question, new HashTag(hashTag)));

        questionTagRepository.save(newQuestionTag);
    }

    @Transactional
    public void deleteQuestionTag(Long questionTagId) {
        QuestionTag questionTag = questionTagRepository.findById(questionTagId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION_TAG));
        questionTagRepository.delete(questionTag);

    }

}
