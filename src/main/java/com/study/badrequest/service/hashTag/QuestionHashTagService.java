package com.study.badrequest.service.hashTag;

import com.study.badrequest.domain.board.HashTag;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionTag;
import com.study.badrequest.repository.board.HashTagRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
import com.study.badrequest.utils.hash_tag.HashTagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionHashTagService {

    private final HashTagRepository hashTagRepository;
    private final QuestionTagRepository questionTagRepository;
    @Transactional
    public void createHashTag(List<String> tags, Question question) {
        log.info("질문 태그 저장 시작");
        ArrayList<QuestionTag> newTags = new ArrayList<>();
        //String -> HashTags
        Set<String> hashTags = tags.stream().map(HashTagUtils::stringToHashTag)
                .collect(Collectors.toSet());

        List<HashTag> alreadySaved = hashTagRepository.findAllByHashTagNameIn(hashTags);

        //새로운 해시태그 생성
        for (HashTag hashTag : alreadySaved) {
            newTags.add(QuestionTag.createQuestionTag(question,hashTag));
        }

        List<String> strings = alreadySaved.stream()
                .map(HashTag::getHashTagName)
                .collect(Collectors.toList());

        Set<HashTag> newHashTags = hashTags.stream()
                .filter(hashTag -> !strings.contains(hashTag))
                .map(HashTag::new)
                .collect(Collectors.toSet());

        List<HashTag> savedNewHashTags = hashTagRepository.saveAll(newHashTags);

        for (HashTag hashTag : savedNewHashTags) {
            newTags.add(QuestionTag.createQuestionTag(question,hashTag));
        }

        questionTagRepository.saveAll(newTags);
    }

    public void removeHashTag(){

    }
}
