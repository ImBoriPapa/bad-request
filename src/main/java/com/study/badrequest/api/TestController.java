package com.study.badrequest.api;

import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.repository.query.BoardDetailDto;
import com.study.badrequest.domain.board.repository.query.BoardQueryRepositoryImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;


@RestController
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final BoardQueryRepositoryImpl boardQueryRepository;

    /**
     * 개시판 조회 쿼리에 index를 추가 했을때와 안했을때 속도 차이 테스트용 API
     */

    @GetMapping("/test")
    public ResponseEntity testQuerySpeed() {

        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSize(100);
        condition.setCategory(Category.COMMUNITY);

        HashMap<String, TimeResult> resultMap = new HashMap<>();
        IntStream.range(0, 100).forEach(i -> resultMap.put("result" + i, getTimeResult()));

        double noticeAvg = calculateAverage(resultMap, TimeResult::getSearchNoticeTime);
        double KnowledgeAvg = calculateAverage(resultMap, TimeResult::getSearchKNOWLEDGETime);
        double questionAvg = calculateAverage(resultMap, TimeResult::getSearchQUESTIONTime);
        double communityAvg = calculateAverage(resultMap, TimeResult::getSearchCOMMUNITYTime);

        ResponseData data = ResponseData.builder()
                .noticeAvg(noticeAvg)
                .knowledgeAvg(KnowledgeAvg)
                .questionAvg(questionAvg)
                .communityAvg(communityAvg)
                .build();

        return ResponseEntity.ok().body(data);
    }

    private double calculateAverage(Map<String, TimeResult> resultMap, ToDoubleFunction<TimeResult> extractor) {
        return resultMap.values().stream().mapToDouble(extractor).average().getAsDouble();
    }

    @Getter
    @NoArgsConstructor
    static class ResponseData {
        private String noticeAvg;
        private String KnowledgeAvg;
        private String questionAvg;
        private String communityAvg;

        @Builder
        public ResponseData(double noticeAvg, double knowledgeAvg, double questionAvg, double communityAvg) {
            this.noticeAvg = noticeAvg + "ms";
            this.KnowledgeAvg = knowledgeAvg + "ms";
            this.questionAvg = questionAvg + "ms";
            this.communityAvg = communityAvg + "ms";
        }
    }

    private TimeResult getTimeResult() {
        StopWatch NOTICE_TIME = new StopWatch();
        StopWatch QUESTION_TIME = new StopWatch();
        StopWatch KNOWLEDGE_TIME = new StopWatch();
        StopWatch COMMUNITY_TIME = new StopWatch();

        long random1 = (int) (Math.random() * (1000 - 1 + 1)) + 1;
        long random2 = (int) (Math.random() * (2000 - 1001 + 1)) + 1001;
        long random3 = (int) (Math.random() * (3000 - 2001 + 1)) + 2001;
        long random4 = (int) (Math.random() * (4000 - 3001 + 1)) + 3001;

        NOTICE_TIME.start();
        BoardDetailDto NOTICE = boardQueryRepository.findBoardDetail(random1, Category.NOTICE).get();
        NOTICE_TIME.stop();

        QUESTION_TIME.start();
        BoardDetailDto QUESTION = boardQueryRepository.findBoardDetail(random2, Category.QUESTION).get();
        QUESTION_TIME.stop();

        KNOWLEDGE_TIME.start();
        BoardDetailDto KNOWLEDGE = boardQueryRepository.findBoardDetail(random3, Category.KNOWLEDGE).get();
        KNOWLEDGE_TIME.stop();

        COMMUNITY_TIME.start();
        BoardDetailDto COMMUNITY = boardQueryRepository.findBoardDetail(random4, Category.COMMUNITY).get();
        COMMUNITY_TIME.stop();

        double noticeResult = NOTICE_TIME.getTotalTimeMillis();
        double questionResult = QUESTION_TIME.getTotalTimeMillis();
        double knowledgeResult = KNOWLEDGE_TIME.getTotalTimeMillis();
        double communityResult = COMMUNITY_TIME.getTotalTimeMillis();

        return TimeResult.builder()
                .searchNoticeTime(noticeResult)
                .searchQUESTIONTime(questionResult)
                .searchKNOWLEDGETime(knowledgeResult)
                .searchCOMMUNITYTime(communityResult)
                .build();
    }

    @Getter
    @NoArgsConstructor
    static class TimeResult {
        private double searchNoticeTime;
        private double searchQUESTIONTime;
        private double searchKNOWLEDGETime;
        private double searchCOMMUNITYTime;

        @Builder
        public TimeResult(double searchNoticeTime, double searchQUESTIONTime, double searchKNOWLEDGETime, double searchCOMMUNITYTime) {
            this.searchNoticeTime = searchNoticeTime;

            this.searchQUESTIONTime = searchQUESTIONTime;

            this.searchKNOWLEDGETime = searchKNOWLEDGETime;

            this.searchCOMMUNITYTime = searchCOMMUNITYTime;

        }


    }
}
