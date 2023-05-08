package com.study.badrequest.contorller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
    @Value("${greeting.server_kind}")
    public String serverKind;

    @Value("${greeting.version}")
    public String version;

    @Value("${greeting.rest_docs}")
    public String restDocs;

    @GetMapping("/")
    public String greeting(Model model, HttpServletRequest request) {

        model.addAttribute("serverKind", serverKind);
        model.addAttribute("version", version);

        return "greeting";
    }

    @GetMapping("/oauth")
    public String oauth() {

        return "oauth/oauth-login";
    }
}
