package com.study.badrequest.member.command.interfaces;

import com.study.badrequest.common.constants.ApiURL;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.member.command.application.MemberAuthenticationService;
import com.study.badrequest.member.command.domain.dto.MemberIssueTemporaryPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberAuthenticationApiController {

    private final MemberAuthenticationService memberAuthenticationService;

    @PostMapping(value = ApiURL.POST_MEMBER_TEMPORARY_PASSWORD_ISSUE_URL)
    public ResponseEntity<?> issueTemporaryPassword(MemberIssueTemporaryPassword memberIssueTemporaryPassword) {

        memberAuthenticationService.issueTemporaryPassword(memberIssueTemporaryPassword);

        return ResponseEntity.ok()
                .body(ApiResponse.success(new Result(true)));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private Boolean success;
    }
}
