package com.study.badrequest.domain.question;

import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.utils.markdown.MarkdownUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class QuestionTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("질문 생성")
    void createQuestion() throws Exception {
        //given
        Question question = Question
                .createQuestion()
                .title("제목입니다.")
                .contents("내용입니다.")
                .build();
        //when
        Question savedQuestion = questionRepository.save(question);
        Question foundQuestion = questionRepository.findById(savedQuestion.getId())
                .orElseThrow(() -> new IllegalArgumentException("Can Not Find Question"));
        //then
        assertThat(foundQuestion.getId()).isEqualTo(savedQuestion.getId());
    }

    @Test
    @DisplayName("질문 생성: 내용의 길이가 100 이상일 경우 프리뷰")
    void createQuestionWithContentsLongerThan100() throws Exception {
        //given
        String markdown = "**안녕하세요!** 오늘은 햇살이 따뜻하게 비추고 있는데, 봄 날씨에 어울리는 신나는 야외 활동을 즐길 수 있을 것 같습니다. **자전거를 타고 한강에서 피크닉을 하거나, 공원에서 친구들과 함께 스케이트보드를 타는 것도 좋을 것 같네요.** 여가 시간을 활용하여 즐거운 추억을 만들어 보세요!\n";

        Question question = Question
                .createQuestion()
                .title("제목입니다.")
                .contents(markdown)
                .build();
        //when
        Question savedQuestion = questionRepository.save(question);
        Question foundQuestion = questionRepository.findById(savedQuestion.getId()).orElseThrow(() -> new IllegalArgumentException("Can Not Find Question"));
        //then
        assertThat(foundQuestion.getPreview()).isEqualTo(MarkdownUtils.markdownToPlainText(markdown).substring(0, 50) + "...");
        System.out.println(foundQuestion.getPreview());
    }

    @Test
    @DisplayName("프리뷰 테스트")
    void previewTest() throws Exception {
        String markdown = "<script>alert('XSS');</script>";
        //given

        //when

        //then


    }


}