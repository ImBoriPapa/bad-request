package com.study.badrequest.question.command.domain;


import com.study.badrequest.question.command.infra.persistence.QuestionTagJpaRepositoryTestConfig;
import com.study.badrequest.testHelper.DatabaseCleaner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuestionTagJpaRepositoryTestConfig.class)
@ActiveProfiles("test")
@Slf4j
class QuestionTagRepositoryIntegrationTest {
    @Autowired
    private QuestionTagRepository questionTagRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void beforeEach() {
        databaseCleaner.clean();
    }

    @Test
    @DisplayName("질문태그저장테스트")
    void 질문태그저장테스트() throws Exception {
        //given
        List<Tag> tags = createSampleTags();

        List<QuestionTag> questionTags = createQuestionTags(tags);
        //when
        List<QuestionTag> saveAll = questionTagRepository.saveAllQuestionTag(questionTags);
        //then
        assertThat(saveAll.size()).isEqualTo(questionTags.size());
    }


    private List<QuestionTag> createQuestionTags(List<Tag> tags) {
        List<QuestionTag> questionTags = new ArrayList<>();
        for (Tag tag : tags) {
            QuestionTag questionTag = QuestionTag.createQuestionTag(tag);
            questionTags.add(questionTag);
        }
        return questionTags;
    }

    private List<Tag> createSampleTags() {

        final String tagName1 = "TagName1";
        final String tagName2 = "TagName2";
        final String tagName3 = "TagName3";
        final String tagName4 = "TagName4";
        final String tagName5 = "TagName5";
        Tag tag1 = Tag.createTag(tagName1);
        Tag tag2 = Tag.createTag(tagName2);
        Tag tag3 = Tag.createTag(tagName3);
        Tag tag4 = Tag.createTag(tagName4);
        Tag tag5 = Tag.createTag(tagName5);
        List<Tag> tags = List.of(tag1, tag2, tag3, tag4, tag5);

        for (Tag tag : tags) {
            entityManager.persist(tag);
        }

        return tags;
    }

}