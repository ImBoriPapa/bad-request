package com.study.badrequest.api.admin;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.admin.AdministratorActivityHistory;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.service.admin.MemberManagementService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberManagementApiController {

    private final MemberManagementService memberManagementService;

    /**
     * 회원 권한 변경
     */
    @PatchMapping("/api/v2/admin/member-management/{memberId}")
    public ResponseEntity changeMemberAuthority(@PathVariable Long memberId,
                                                @RequestBody ChangeAuthorityRequest form,
                                                @LoggedInMember CurrentLoggedInMember.Information information) {

        if (information.getAuthority() != Authority.ADMIN) {
            throw new CustomRuntimeException(ApiResponseStatus.PERMISSION_DENIED);
        }

        AdministratorActivityHistory history = memberManagementService.changeMemberAuthority(information.getId(), memberId, form.getAuthority(), form.getReason());

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(new ChangeAuthorityResponse(history.getActivityAt())));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangeAuthorityRequest {
        private Authority authority;
        private String reason;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChangeAuthorityResponse {
        private LocalDateTime changedAt;
    }

}
