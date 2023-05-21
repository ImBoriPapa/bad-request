package com.study.badrequest.repository.board;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.board.*;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.dto.board.BoardSearchCondition;
import com.study.badrequest.repository.board.query.BoardListDto;
import com.study.badrequest.repository.board.query.BoardListResult;
import com.study.badrequest.repository.board.query.TagDto;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.testHelper.TestConfig;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.stream.Collectors;

import static com.study.badrequest.domain.board.QBoard.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class, BoardQueryRepositoryImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class BoardQueryRepositoryTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    private BoardQueryRepository boardQueryRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private HashTagRepository hashTagRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("withTag")
    void withTag() throws Exception {
        //given
        initSampleData();

        //when
        em.flush();
        em.clear();
        //then

        BoardSearchCondition condition = new BoardSearchCondition();

        BoardListDto boardList = boardQueryRepository.findBoardList(condition,null);

        for (BoardListResult result : boardList.getResults()) {
            log.info("id: {}", result.getId());
            log.info("memberId: {}", result.getMemberId());
            log.info("profileImage: {}", result.getProfileImage());
            log.info("nickname: {}", result.getNickname());
            log.info("title: {}", result.getTitle());
            log.info("likeCount: {}", result.getLikeCount());
            log.info("category: {}", result.getCategory());
            log.info("topic: {}", result.getTopic());
            log.info("commentCount: {}", result.getCommentCount());
            for (TagDto tag : result.getHashTags()) {
                log.info("hashTags: {}", tag.getTagName());
            }
            log.info("createdAt: {}", result.getCreatedAt());
        }
    }

    public void initSampleData() {
        log.info("데이터 입력 시작");


        List<String> titles = List.of(
                "Java Basics",
                "JavaScript Frameworks Comparison",
                "Python for Data Science",
                "MySQL Performance Tuning",
                "Introduction to MongoDB",
                "Community Guidelines"
        );

        List<String> contents = List.of(
                "Java is a popular programming language...",
                "There are many JavaScript frameworks available...",
                "Python is widely used in the field of data science...",
                "MySQL is a popular relational database management system...",
                "MongoDB is a document-based NoSQL database...",
                "Welcome to our community! Here are our guidelines..."
        );

        List<Topic> topics = List.of(
                Topic.JAVA,
                Topic.JAVASCRIPT,
                Topic.PYTHON,
                Topic.MYSQL,
                Topic.MONGODB
        );

        List<Category> categories = List.of(
                Category.NOTICE,
                Category.QUESTION,
                Category.KNOWLEDGE,
                Category.COMMUNITY
        );

        List<Member> members = new ArrayList<>();
        for (int i = 10; i >= 1; i--) {
            String email = "email" + i + "@email.com";
            Member member = Member.builder()
                    .email(email)
                    .authority(Authority.MEMBER)
                    .memberProfile(new MemberProfile("닉네임" + i, ProfileImage.createDefault("image")))
                    .build();
            members.add(memberRepository.save(member));
        }

        for (int i = 1; i <= 100; i++) {
            String title = titles.get((i - 1) % titles.size()) + " " + i;
            String content = contents.get((i - 1) % contents.size()) + " " + i;
            Topic topic = topics.get((i - 1) % topics.size());
            Category category = categories.get((i - 1) % categories.size());
            Member member = members.get((i - 1) % members.size());

            List<HashTag> tags = new ArrayList<>();
            for (int j = 1; j <= (i % 4) + 2; j++) {
                HashTag tag = new HashTag("tag" + j);
                tags.add(hashTagRepository.save(tag));
            }

            Board board = Board.createBoard()
                    .title(title)
                    .contents(content)
                    .topic(topic)
                    .category(category)
                    .member(member)
                    .build();

            tags.forEach(tag -> {
                BoardTag.createBoardTag(board,tag);
            });

            boardRepository.save(board);
        }
        log.info("데이터 입력 완료");
    }


}
