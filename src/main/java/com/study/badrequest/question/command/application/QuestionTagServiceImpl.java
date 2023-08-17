package com.study.badrequest.question.command.application;

import com.study.badrequest.question.command.domain.Tag;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionTag;
import com.study.badrequest.question.query.interfaces.QuestionTagResponse;
import com.study.badrequest.common.exception.CustomRuntimeException;

import com.study.badrequest.question.command.domain.TagRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.question.command.domain.QuestionTagRepository;
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

import static com.study.badrequest.common.response.ApiResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionTagServiceImpl implements QuestionTagService {

    private final QuestionTagRepository questionTagRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public QuestionTagResponse.Create createQuestionTagProcessing(Long questionId, List<String> tags) {
        log.info("createQuestionTagProcessing() - questionID: {}", questionId);
        validateTags(tags);
        log.info("requested Tag name: {}", tags.toArray());

        final Question question = getQuestion(questionId);

        final Set<String> hashtagNames = convertToHashTagNames(tags);

        final List<QuestionTag> questionTags = saveQuestionTags(question, hashtagNames);

        return new QuestionTagResponse.Create(getQuestionTagIds(questionTags));
    }

    private List<QuestionTag> saveQuestionTags(Question question, Set<String> hashtagNames) {
        final List<QuestionTag> questionTags;
        final List<Tag> existsTags = findExistsHashTagByHashTagNames(hashtagNames);

        if (existsTags.isEmpty()) {
            questionTags = saveQuestionTagsWithNewHashTagNames(question, hashtagNames);
        } else
            questionTags = saveQuestionTagsWithNewHashTagNamesAndExistHashTags(question, hashtagNames, existsTags);

        return questionTags;
    }

    private List<Long> getQuestionTagIds(List<QuestionTag> questionTags) {
        return questionTags.stream().map(QuestionTag::getId).collect(Collectors.toList());
    }

    private List<Tag> findExistsHashTagByHashTagNames(Set<String> hashtagNames) {
        return tagRepository.findAllByNameIn(hashtagNames);
    }

    private Set<String> convertToHashTagNames(List<String> tags) {
        return tags.stream()
                .map(HashTagUtils::stringToHashTagString)
                .collect(Collectors.toSet());
    }

    private void validateTags(List<String> tags) {
        if (tags == null || tags.isEmpty() || tags.size() > 5) {
            throw CustomRuntimeException.createWithApiResponseStatus(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED);
        }
    }

    private Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION));
    }

    private List<QuestionTag> saveQuestionTagsWithNewHashTagNamesAndExistHashTags(Question question, Set<String> newHastTagNames, List<Tag> existsHashTas) {
        Map<String, QuestionTag> haveToSave = new HashMap<>();

        addQuestionTagsToHaveToSave(question, existsHashTas, haveToSave);

        addQuestionTagsToHaveToSave(question, createNewHashTags(newHastTagNames, haveToSave), haveToSave);

        return questionTagRepository.saveAllQuestionTag(haveToSave.values());
    }

    private void addQuestionTagsToHaveToSave(Question question, List<Tag> existsHashTas, Map<String, QuestionTag> haveToSave) {
    }

    private List<Tag> createNewHashTags(Set<String> newHastTagNames, Map<String, QuestionTag> haveToSave) {
        return createHashTagsWithHashTagNames(newHastTagNames)
                .stream()
                .filter(hashTag -> !haveToSave.containsKey(hashTag.getName()))
                .collect(Collectors.toList());
    }

    private List<QuestionTag> saveQuestionTagsWithNewHashTagNames(Question question, Set<String> newHashTagNames) {
        return null;
    }

    private List<Tag> createHashTagsWithHashTagNames(Set<String> requestedTags) {
        return requestedTags.stream()
                .map(Tag::createTag)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public QuestionTagResponse.Add addQuestionTagProcessing(Long questionId, String questionTag) {
        log.info("addQuestionTagProcessing");


        return null;
    }

    @Transactional
    public void deleteQuestionTag(Long questionTagId) {

    }

}
