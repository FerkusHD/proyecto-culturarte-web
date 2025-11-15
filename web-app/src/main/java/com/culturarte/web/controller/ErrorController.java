package com.culturarte.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @RequestMapping("/error")
    public String error(Model model) {
        logger.error("Error page accessed");
        return "error";
    }

    @RequestMapping("/error/404")
    public String error404(Model model) {
        logger.warn("404 error page accessed");
        if (!model.containsAttribute("mensajeError")) {
            model.addAttribute("mensajeError", "Recurso no encontrado");
        }
        return "error/404";
    }
}

