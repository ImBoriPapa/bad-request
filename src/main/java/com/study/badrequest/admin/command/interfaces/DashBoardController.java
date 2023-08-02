package com.study.badrequest.admin.command.interfaces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashBoardController {

    @GetMapping("/dashboard")
    public String toBoard() {

        return "admin/dashboard";
    }

}
