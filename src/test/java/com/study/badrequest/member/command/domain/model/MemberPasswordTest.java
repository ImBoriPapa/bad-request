package com.study.badrequest.member.command.domain.model;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.values.PasswordType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberPasswordTest {

    @Test
    @DisplayName("password 가 Null")
    void test1() throws Exception {
        //given
        String password = null;
        //when

        //then
        assertThatThrownBy(() -> new MemberPassword(password, PasswordType.AVAILABLE, LocalDateTime.now()))
                .isInstanceOf(CustomRuntimeException.class);

    }

}