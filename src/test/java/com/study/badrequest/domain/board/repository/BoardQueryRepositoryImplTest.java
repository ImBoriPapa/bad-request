package com.study.badrequest.domain.board.repository;

import com.study.badrequest.base.BaseMemberTest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional
class BoardQueryRepositoryImplTest extends BaseMemberTest {


}