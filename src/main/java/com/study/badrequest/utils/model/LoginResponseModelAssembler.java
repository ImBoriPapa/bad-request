package com.study.badrequest.utils.model;

import com.study.badrequest.api.LoginController;
import com.study.badrequest.domain.login.dto.LoginResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseModelAssembler {


    public EntityModel<LoginResponse.LoginResult> toModel(LoginResponse.LoginResult result) {

        return EntityModel.of(result)
                .add(WebMvcLinkBuilder.linkTo(LoginController.class).slash("/log-out").withRel("POST: 로그아웃"))
                .add(WebMvcLinkBuilder.linkTo(LoginController.class).slash("/refresh").withRel("POST: 토큰재발급"));
    }

    public EntityModel<LoginResponse.LogoutResult> toModel(LoginResponse.LogoutResult result) {

        return EntityModel.of(result)
                .add(WebMvcLinkBuilder.linkTo(LoginController.class).slash("/login").withRel("POST : 로그인"));
    }

    public EntityModel<LoginResponse.ReIssueResult> toModel(LoginResponse.ReIssueResult result) {

        return EntityModel.of(result);

    }
}
