package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.member.command.interfaces.LoginController;
import com.study.badrequest.member.command.interfaces.MemberAccountApiController;
import com.study.badrequest.member.command.interfaces.MemberProfileApiController;
import com.study.badrequest.member.query.interfaces.MemberQueryApiController;

import com.study.badrequest.member.command.interfaces.MemberResponse;
import com.study.badrequest.member.query.dto.MemberDetailDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class MemberResponseModelAssembler {

    public EntityModel<MemberResponse.Update> changeNicknameModel(MemberResponse.Update form) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberProfileApiController.class).changeNickname(form.getId(), null, null, null, null)).withSelfRel()
        );

        return EntityModel.of(form, links);
    }

    public EntityModel<MemberResponse.SendAuthenticationEmail> getSendAuthenticationMail(MemberResponse.SendAuthenticationEmail email) {
        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).sendAuthenticationEmail(null, null)).withSelfRel()
        );
        return EntityModel.of(email, links);
    }

    public EntityModel<MemberResponse.Update> getChangePasswordModel(MemberResponse.Update update) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).patchPassword(update.getId(), null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberQueryApiController.class).getProfile(null, null)).withRel("Profile")
        );

        return EntityModel.of(update, links);
    }

    public EntityModel<MemberResponse.Update> getChangeContactModel(MemberResponse.Update update) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).patchContact(update.getId(), null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberQueryApiController.class).getProfile(null, null)).withRel("Profile")
        );

        return EntityModel.of(update, links);
    }


    public EntityModel<MemberResponse.Delete> getDeleteModel(MemberResponse.Delete result) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).deleteMember(null, null, null, null, null)).withSelfRel(),
                linkTo(methodOn(MemberAccountApiController.class).createMember(null, null, null)).withRel("Signup Member")
        );

        return EntityModel.of(result, links);
    }

    public EntityModel<MemberResponse.TemporaryPassword> getIssuePasswordModel(MemberResponse.TemporaryPassword password) {

        List<Link> links = List.of(
                linkTo(methodOn(MemberAccountApiController.class).issueTemporaryPassword(null, null, null)).withSelfRel(),
                linkTo(methodOn(LoginController.class).loginByEmail(null, null, null)).withRel("Login")
        );

        return EntityModel.of(password, links);
    }



    public EntityModel<MemberDetailDto> retrieveMemberModel(MemberDetailDto memberDetailDto) {

        return EntityModel.of(memberDetailDto)
                .add(linkTo(methodOn(MemberQueryApiController.class).retrieveMemberAccount(null, memberDetailDto.getId())).withSelfRel())
                .add(linkTo(methodOn(MemberProfileApiController.class).changeNickname(memberDetailDto.getId(), null, null, null, null)).withRel("Change Nickname"))
                .add(linkTo(methodOn(MemberAccountApiController.class).patchContact(memberDetailDto.getId(), null, null, null, null)).withRel("Change Contact"))
                .add(linkTo(methodOn(MemberProfileApiController.class).changeIntroduce(memberDetailDto.getId(), null, null)).withRel("Change Self-Introduce"))
                .add(linkTo(methodOn(MemberProfileApiController.class).changeProfileImage(memberDetailDto.getId(), null, null)).withRel("Change Profile Image"))
                .add(linkTo(methodOn(MemberProfileApiController.class).deleteProfileImage(memberDetailDto.getId(), null)).withRel("Change Profile Image To Default"))
                .add(linkTo(methodOn(MemberAccountApiController.class).patchPassword(memberDetailDto.getId(), null, null, null, null)).withRel("Change Password"))
                .add(linkTo(methodOn(MemberAccountApiController.class).deleteMember(memberDetailDto.getId(), null, null, null, null)).withRel("Withdrawing Member"));
    }

    public EntityModel<MemberResponse.Update> changeProfileImageModel(MemberResponse.Update response) {

        List<Link> links = List.of();

        return EntityModel.of(response, links);
    }
}
