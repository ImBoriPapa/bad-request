package com.study.badrequest.health;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestAPI {

    private final JwtUtils jwtUtils;

    @GetMapping("/test/teacher")
    public ResponseEntity authorityTestApi(@RequestHeader(value = AUTHORIZATION_HEADER) String accessToken) {
        log.info("teacher 권한 필요 API");

        Authentication authentication = jwtUtils.getAuthentication(accessToken.substring(7));

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roll -> roll.equals("ROLL_ADMIN"));

        Map<String, String> result = new HashMap<>();

        result.put("authority", "강사 권한이 있습니다.");

        if (isAdmin) {
            result.replace("authority", "관리자 권한이 있습니다.");
        }

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, result));
    }
}
