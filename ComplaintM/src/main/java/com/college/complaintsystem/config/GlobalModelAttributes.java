package com.college.complaintsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {
        String currentPath = request.getRequestURI();
        model.addAttribute("currentPath", currentPath != null ? currentPath : "");
    }
}
