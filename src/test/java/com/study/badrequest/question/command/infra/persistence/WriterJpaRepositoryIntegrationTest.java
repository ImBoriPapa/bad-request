package com.study.badrequest.question.command.infra.persistence;


import com.study.badrequest.question.command.domain.Writer;
import com.study.badrequest.question.command.domain.WriterRepository;
import com.study.badrequest.question.command.domain.WriterType;
import com.study.badrequest.testHelper.DatabaseCleaner;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(WriterJpaRepositoryTestConfig.class)
@ActiveProfiles("test")
@Slf4j
class WriterJpaRepositoryIntegrationTest {

    @Autowired
    private WriterRepository writerRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void beforeEach() {
        databaseCleaner.clean();
    }

    @Test
    @DisplayName("작성자 저장 테스트")
    void 작성자저장테스트() throws Exception {
        //given
        final Long memberId = 214124L;
        final String nickname = "nickname";
        final String profileImage = "image";
        final Integer activityScore = 150;
        final WriterType writerType = WriterType.MEMBER;
        Writer writer = Writer.createWriter(memberId, nickname, profileImage, activityScore, writerType);
        //when
        Writer saved = writerRepository.save(writer);
        Writer find = writerRepository.findById(saved.getId()).get();
        //then
        assertThat(find.getId()).isEqualTo(saved.getId());
    }

}