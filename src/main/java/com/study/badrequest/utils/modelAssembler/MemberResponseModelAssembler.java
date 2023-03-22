package com.study.badrequest.utils.modelAssembler;

import com.study.badrequest.api.login.LoginController;
import com.study.badrequest.api.member.MemberCommandController;
import com.study.badrequest.api.member.MemberQueryController;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.query.MemberAuthDto;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.repository.query.MemberDetailDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class MemberResponseModelAssembler {
    /**
     * postMember
     */
    public EntityModel<MemberResponse.Create> toModel(MemberResponse.Create result) {
        return EntityModel.of(result)
                .add(linkTo(LoginController.class).slash("/login").withRel("Login"));
    }

    /**
     * patchPassword,patchContact
     */
    public EntityModel<MemberResponse.UpdateResult> toModel(MemberResponse.UpdateResult result) {
        return EntityModel.of(result)
                .add(linkTo(methodOn(MemberQueryController.class).getMember(null, result.getId())).withRel("GET: 회원 정보"));
    }

    /**
     * deleteMember
     */
    public EntityModel<MemberResponse.DeleteResult> toModel(MemberResponse.DeleteResult result) {
        return EntityModel.of(result)
                .add(linkTo(methodOn(MemberCommandController.class).createMember(null, null)).withRel("POST: 회원가입"));
    }

    // TODO: 2023/02/11 응답값 추가
    public EntityModel<MemberResponse.AuthResult> toModel(MemberAuthDto result) {
        return EntityModel.of(new MemberResponse.AuthResult(result.getId(), result.getAuthority()));
    }

    /**
     * Member Resource 생성 위치
     */
    public URI getLocationUri(Long memberId) {
        return linkTo(methodOn(MemberQueryController.class).getMember(null, memberId)).toUri();
    }

    public EntityModel<MemberDetailDto> toModel(MemberDetailDto memberDetailDto, Authority authority) {

        if (authority == Authority.ADMIN) {
            return EntityModel.of(memberDetailDto)
                    .add(linkTo(methodOn(MemberCommandController.class).patchContact(memberDetailDto.getId(), null, null)).withRel("PATCH : 연락처 변경"));
        }

        return EntityModel.of(memberDetailDto)
                .add(linkTo(methodOn(MemberCommandController.class).patchContact(memberDetailDto.getId(), null, null)).withRel("PATCH : 연락처 변경"))
                .add(linkTo(methodOn(MemberCommandController.class).patchPassword(memberDetailDto.getId(), null, null)).withRel("PATCH : 비밀번호 변경"))
                .add(linkTo(methodOn(MemberCommandController.class).deleteMember(memberDetailDto.getId(), null, null)).withRel("DELETE : 회원 탈퇴"));
    }
}
