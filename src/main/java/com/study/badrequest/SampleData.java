package com.study.badrequest;


import com.study.badrequest.domain.board.*;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;

import com.study.badrequest.domain.question.Question;
import com.study.badrequest.dto.question.QuestionRequest;

import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.repository.board.HashTagRepository;
import com.study.badrequest.repository.member.MemberRepository;

import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.service.answer.AnswerServiceImpl;
import com.study.badrequest.service.question.QuestionService;
import com.study.badrequest.utils.hash_tag.HashTagUtils;
import com.study.badrequest.utils.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile({"dev","prod"})
@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SampleData {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3ImageUploader imageUploader;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final ArrayList<Member> memberList = new ArrayList<>();
    private final ArrayList<Question> questions = new ArrayList<>();
    private final HashTagRepository hashTagRepository;
    private final AnswerServiceImpl answerService;
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        createMemberData();
        createSampleHashTag();
        createSampleQuestion();
    }

    public void createMemberData() {
        System.out.println("================== SAMPLE MEMBER INIT START==================");

        List<String> domainList = List.of("@gmail.com", "@naver.com", "@daum.com", "@icloud.com");

        IntStream intStream = IntStream.rangeClosed(1, 10);

        Member sampleMember = Member.createSelfRegisteredMember(
                "sample@gmail.com",
                passwordEncoder.encode("sample1234!@"),
                "01011111111",
                new MemberProfile("샘플유저", ProfileImage.createDefault(imageUploader.getDefaultProfileImage()))
        );

        memberList.add(sampleMember);

        intStream.forEach(index -> {

            String email = "test";
            String domain = domainList.get(new Random().nextInt(domainList.size()));
            String nickname = "테스터" + index;
            String password = "tester!@";
            String contact = "010" + String.format("%08d", new Random().nextInt(90000000) + 10000000);


            Member member = Member.createSelfRegisteredMember(
                    email + index + domain,
                    passwordEncoder.encode(password),
                    contact,
                    new MemberProfile(nickname + index, ProfileImage.createDefault(imageUploader.getDefaultProfileImage()))
            );
            memberList.add(member);
        });

        memberRepository.saveAll(memberList);
        System.out.println("================== SAMPLE MEMBER INIT FINISH ==================");
    }

    public void createSampleHashTag() {
        List<String> tagStrings = List.of("Java", "Spring", "MySQL", "JPA", "DataBase", "JavaScript", "MongoDB", "MariaDB", "React", "Node.js");

        List<HashTag> hashTags = tagStrings.stream()
                .map(HashTagUtils::stringToHashTagString)
                .map(HashTag::new)
                .collect(Collectors.toList());

        hashTagRepository.saveAll(hashTags);
    }

    public void createSampleQuestion() {

        Map<String, List<String>> topicsByTechnology = new HashMap<>();
        topicsByTechnology.put("Java", List.of("Java Basics", "Java Collections", "Java Concurrency", "Java IO", "Java Networking"));
        topicsByTechnology.put("Python", List.of("Python Basics", "Python Data Structures", "Python Algorithms", "Python Libraries", "Python Web Development"));
        topicsByTechnology.put("C++", List.of("C++ Basics", "C++ Pointers and References", "C++ Templates", "C++ Standard Library", "C++ Graphics"));
        topicsByTechnology.put("JavaScript", List.of("JavaScript Basics", "JavaScript DOM Manipulation", "JavaScript Frameworks", "JavaScript Libraries", "JavaScript Node.js"));
        topicsByTechnology.put("HTML", List.of("HTML Basics", "HTML Forms", "HTML Tables", "HTML5 APIs", "HTML Accessibility"));
        topicsByTechnology.put("CSS", List.of("CSS Basics", "CSS Layout", "CSS Animations", "CSS Frameworks", "CSS Responsive Design"));
        topicsByTechnology.put("React", List.of("React Basics", "React Hooks", "React Redux", "React Router", "React Performance"));
        topicsByTechnology.put("Angular", List.of("Angular Basics", "Angular Components", "Angular Directives", "Angular Services", "Angular Forms"));
        topicsByTechnology.put("Vue.js", List.of("Vue.js Basics", "Vue.js Components", "Vue.js Directives", "Vue.js Routing", "Vue.js State Management"));
        topicsByTechnology.put("Node.js", List.of("Node.js Basics", "Node.js Express", "Node.js MongoDB", "Node.js WebSocket", "Node.js Testing"));
        topicsByTechnology.put("SQL", List.of("SQL Basics", "SQL Joins", "SQL Aggregation", "SQL Indexes", "SQL Stored Procedures"));
        topicsByTechnology.put("MongoDB", List.of("MongoDB Basics", "MongoDB CRUD Operations", "MongoDB Aggregation", "MongoDB Indexing", "MongoDB Performance"));

        List<QuestionRequest.Create> creates = new ArrayList<>(10000);
        String[] titlePrefixes = {"What is", "How to", "Why do", "When should", "Where to"};
        String[] contentPrefixes = {"I am trying to learn", "I have encountered an issue with", "Can someone explain", "I want to know more about", "I am interested in"};
        String[] technologies = {"Java", "Python", "C++", "JavaScript", "HTML", "CSS", "React", "Angular", "Vue.js", "Node.js", "SQL", "MongoDB"};
        Random rand = new Random();

        for (int i = 1; i <= 100; i++) {
            String technology = technologies[rand.nextInt(technologies.length)];
            String titlePrefix = titlePrefixes[rand.nextInt(titlePrefixes.length)];
            String contentPrefix = contentPrefixes[rand.nextInt(contentPrefixes.length)];
            String topic = topicsByTechnology.get(technology).get(rand.nextInt(5));
            String title = titlePrefix + " " + technology + " ?";
            String content = contentPrefix + " " + technology + ". This is question number " + i;
            creates.add(new QuestionRequest.Create(title, content, List.of(technology, topic),null));
        }

        ArrayList<Long> ids = new ArrayList<>();

        creates.forEach(form -> {
            Member member = memberList.get(new Random().nextInt(10) + 1);
            QuestionResponse.Create create = questionService.createQuestion(member.getId(), form);
            ids.add(create.getId());
        });

        questions.addAll(questionRepository.findAllById(ids));

    }



}
