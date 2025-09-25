package com.culturarte.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/")
    public String index() {
        return "index"; // /WEB-INF/jsp/index.jsp
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu"; // /WEB-INF/jsp/menu.jsp
    }
}
