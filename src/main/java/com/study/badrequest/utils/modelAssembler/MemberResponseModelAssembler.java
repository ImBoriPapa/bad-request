package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.login.LoginController;
import com.study.badrequest.api.member.MemberApiController;
import com.study.badrequest.api.member.MemberProfileApiController;
import com.study.badrequest.api.member.MemberQueryApiController;
import com.study.badrequest.commons.hateoas.ResponseModelAssembler;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.repository.member.query.MemberDetailDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class MemberResponseModelAssembler extends ResponseModelAssembler {
    public EntityModel<MemberResponse.Create> createMemberModel(MemberResponse.Create create) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberApiController.class).createMember(null, null)).withSelfRel(),
                linkTo(methodOn(LoginController.class).loginByEmail(null, null, null)).withRel("Login")
        );

        return createEntityModel(create, links);
    }

    public EntityModel<MemberResponse.Update> changeNicknameModel(MemberResponse.Update form) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberProfileApiController.class).changeNickname(form.getId(), null, null, null)).withSelfRel()
        );

        return createEntityModel(form, links);
    }

    public EntityModel<MemberResponse.SendAuthenticationEmail> getSendAuthenticationMail(MemberResponse.SendAuthenticationEmail email) {
        List<Link> links = List.of(
                linkTo(methodOn(MemberApiController.class).sendAuthenticationEmail(null, null)).withSelfRel()
        );
        return createEntityModel(email, links);
    }

    public EntityModel<MemberResponse.Update> getChangePasswordModel(MemberResponse.Update update) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberApiController.class).patchPassword(update.getId(), null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberQueryApiController.class).getProfile(null, null)).withRel("Profile")
        );

        return createEntityModel(update, links);
    }

    public EntityModel<MemberResponse.Update> getChangeContactModel(MemberResponse.Update update) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberApiController.class).patchContact(update.getId(), null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberQueryApiController.class).getProfile(null, null)).withRel("Profile")
        );

        return createEntityModel(update, links);
    }


    public EntityModel<MemberResponse.Delete> getDeleteModel(MemberResponse.Delete result) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberApiController.class).deleteMember(null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberApiController.class).createMember(null, null)).withRel("Signup Member")
        );

        return createEntityModel(result, links);
    }

    public EntityModel<MemberResponse.TemporaryPassword> getIssuePasswordModel(MemberResponse.TemporaryPassword password) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberApiController.class).issueTemporaryPassword(null, null)).withSelfRel(),
                linkTo(methodOn(LoginController.class).loginByEmail(null, null, null)).withRel("Login")
        );

        return createEntityModel(password, links);
    }

    /**
     * Member Resource 생성 위치
     */
    public URI getLocationUri(Long memberId) {
        return linkTo(methodOn(MemberQueryApiController.class).retrieveMemberAccount(null, memberId)).toUri();
    }

    public EntityModel<MemberDetailDto> retrieveMemberModel(MemberDetailDto memberDetailDto) {

        return EntityModel.of(memberDetailDto)
                .add(linkTo(methodOn(MemberQueryApiController.class).retrieveMemberAccount(null, memberDetailDto.getId())).withSelfRel())
                .add(linkTo(methodOn(MemberProfileApiController.class).changeNickname(memberDetailDto.getId(), null, null, null)).withRel("Change Nickname"))
                .add(linkTo(methodOn(MemberApiController.class).patchContact(memberDetailDto.getId(), null, null, null)).withRel("Change Contact"))
                .add(linkTo(methodOn(MemberProfileApiController.class).changeIntroduce(memberDetailDto.getId(), null)).withRel("Change Self-Introduce"))
                .add(linkTo(methodOn(MemberProfileApiController.class).changeProfileImage(memberDetailDto.getId(), null)).withRel("Change Profile Image"))
                .add(linkTo(methodOn(MemberProfileApiController.class).deleteProfileImage(memberDetailDto.getId())).withRel("Change Profile Image To Default"))
                .add(linkTo(methodOn(MemberApiController.class).patchPassword(memberDetailDto.getId(), null, null, null)).withRel("Change Password"))
                .add(linkTo(methodOn(MemberApiController.class).deleteMember(memberDetailDto.getId(), null, null, null)).withRel("Withdrawing Member"));
    }

    public EntityModel<MemberResponse.Update> changeProfileImageModel(MemberResponse.Update response) {

        List<Link> links = List.of();

        return EntityModel.of(response, links);
    }
}
