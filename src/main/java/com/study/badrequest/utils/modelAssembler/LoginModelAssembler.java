package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.login.command.interfaces.LoginController;
import com.study.badrequest.member.query.interfaces.MemberQueryApiController;
import com.study.badrequest.login.command.interfaces.LoginResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LoginModelAssembler {
    public EntityModel<LoginResponse.LoginResult> createLoginModel(LoginResponse.LoginResult result) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberQueryApiController.class).getLoggedInInformation(result.getMemberId(), null)).withRel("login user information"),
                linkTo(methodOn(LoginController.class).logout(null, null)).withRel("logout"),
                linkTo(methodOn(LoginController.class).reIssueToken(null, null)).withRel("reIssue token")
        );

        return EntityModel.of(result).add(links);
    }

    public EntityModel<LoginResponse.LogoutResult> createLogoutModel(LoginResponse.LogoutResult result) {

        return EntityModel.of(result)
                .add(linkTo(methodOn(LoginController.class).logout(null, null)).withSelfRel());
    }

    public EntityModel<LoginResponse.ReIssueResult> createReissueModel(LoginResponse.ReIssueResult result) {
        List<Link> links = List.of(
                linkTo(methodOn(MemberQueryApiController.class).getLoggedInInformation(result.getMemberId(), null)).withRel("login user information"),
                linkTo(methodOn(LoginController.class).logout(null, null)).withRel("logout")
        );
        return EntityModel.of(result).add(links);

    }
}
