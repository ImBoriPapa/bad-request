package com.study.badrequest.admin.command.interfaces;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RecordManagementApiController {

    @GetMapping("")
    public ResponseEntity getMemberRecord() {



        return ResponseEntity
                .ok()
                .body(null);
    }

}
