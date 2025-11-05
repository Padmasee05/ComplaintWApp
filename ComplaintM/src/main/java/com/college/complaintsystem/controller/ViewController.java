package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.college.complaintsystem.model.Student;
import com.college.complaintsystem.repository.StudentRepository;
import com.college.complaintsystem.service.StudentService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class ViewController {
	  @Autowired
	    private StudentService studentService;
	  

	 @GetMapping("/register")
	    public String showRegisterPage(Model model) {
	        model.addAttribute("student", new Student()); 
	        return "register"; // renders register.html
	    }
	 
	 @PostMapping("/register")
	 public String processRegisterForm(
	         @Valid @ModelAttribute("student") Student student,
	         BindingResult result,
	         Model model) {

	     // If there are validation errors → stay on the form
	     if (result.hasErrors()) {
	         model.addAttribute("errorMessage", "Please fix the errors below.");
	         return "register"; // shows register.html with error messages
	     }

	     try {
	         studentService.saveStudent(student);
	     } catch (IllegalArgumentException e) {
	         model.addAttribute("errorMessage", e.getMessage());
	         return "register";
	     }
	     return "redirect:/login";
	 }



    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; 
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // ✅ Clears all session data
        return "redirect:/login"; // ✅ Redirects back to login page
    }


}
