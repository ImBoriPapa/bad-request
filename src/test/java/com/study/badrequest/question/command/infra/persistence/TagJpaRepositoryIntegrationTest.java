package com.study.badrequest.question.command.infra.persistence;


import com.study.badrequest.question.command.domain.Tag;
import com.study.badrequest.question.command.domain.TagRepository;
import com.study.badrequest.testHelper.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TagJpaRepositoryTestConfig.class)
class TagJpaRepositoryIntegrationTest {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void before() {
        databaseCleaner.clean();
    }

    @Test
    @DisplayName("test")
    void test() throws Exception {
        //given
        final String tag = "Java";
        Tag hashTag = Tag.createTag(tag);
        //when
        Tag save = tagRepository.save(hashTag);
        Tag find = tagRepository.findById(save.getId()).get();
        //then
        assertThat(find.getId()).isEqualTo(save.getId());
    }
}