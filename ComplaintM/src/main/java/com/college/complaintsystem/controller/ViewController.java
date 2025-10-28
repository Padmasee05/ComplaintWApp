package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.college.complaintsystem.model.Student;
import com.college.complaintsystem.repository.StudentRepository;

@Controller
public class ViewController {
	 @Autowired
	    private StudentRepository studentRepository;

	 @GetMapping("/register")
	    public String showRegisterPage(Model model) {
	        model.addAttribute("student", new Student()); 
	        return "register"; // renders register.html
	    }
	 
	 @PostMapping("/register")
	    public String processRegisterForm(@ModelAttribute("student") Student student) {
	        studentRepository.save(student);
	        return "redirect:/login"; 
	    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // We'll create login.html next
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // sample placeholders — replace with real service calls later
        model.addAttribute("student", new Student()); // or fetch logged-in student
        model.addAttribute("totalComplaints", 0);
        model.addAttribute("urgentComplaints", 0);
        model.addAttribute("resolvedComplaints", 0);
        model.addAttribute("myComplaints", new java.util.ArrayList<>());
        model.addAttribute("recentActivity", new java.util.ArrayList<>());
        model.addAttribute("frequentComplaints", new java.util.ArrayList<>()); // List<Object[]> later
        return "dashboard";
    }

}
