package com.college.complaintsystem.controller;

import com.college.complaintsystem.model.Student;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;  
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.service.ComplaintService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, HttpServletRequest request, Model model) {
        // get logged-in student from session
        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }
        
        

        // Fetch user-specific data
        List<Complaint> userComplaints = complaintService.getComplaintsByStudentId(student.getId());
        long pendingCount   = userComplaints.stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count();
        long resolvedCount  = userComplaints.stream().filter(c -> "Resolved".equalsIgnoreCase(c.getStatus())).count();

        // Frequent complaints across all students
        List<Complaint> allComplaints = complaintService.getAllComplaints();
        Map<String, Long> categoryFrequency = allComplaints.stream()
                .collect(Collectors.groupingBy(Complaint::getCategory, Collectors.counting()));

        // top 3 most frequent categories
        List<Map.Entry<String, Long>> frequentComplaints = categoryFrequency.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(3)
                .toList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        List<String> recentActivity = userComplaints.stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(5)
                .map(c -> "Complaint #" + c.getId() + " (" + c.getCategory() + ") - " +
                        c.getStatus() + " • " + c.getUpdatedAt().format(formatter))
                .toList();
        	


        // Add data to model
        model.addAttribute("recentActivity", recentActivity);
        model.addAttribute("student", student);
        model.addAttribute("userComplaints", userComplaints);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("frequentComplaints", frequentComplaints);
        model.addAttribute("currentPath", request.getRequestURI());
        return "dashboard";
    }
    
    @GetMapping("/search")
    public String searchComplaints(String keyword,HttpSession session,HttpServletRequest request, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        List<Complaint> allComplaints = complaintService.getComplaintsByStudentId(student.getId());

        // Filter complaints based on keyword
        List<Complaint> filteredComplaints = allComplaints.stream()
                .filter(c ->
                        c.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        c.getCategory().toLowerCase().contains(keyword.toLowerCase()) ||
                        c.getStatus().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();

        model.addAttribute("userComplaints", filteredComplaints);
        model.addAttribute("student", student);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pendingCount", filteredComplaints.stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("resolvedCount", filteredComplaints.stream().filter(c -> "Resolved".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("currentPath", request.getRequestURI());

        return "dashboard";
    }
    
    @GetMapping("/complaint/register")
    public String showComplaintRegisterPage(HttpServletRequest request, Model model) {
    	model.addAttribute("currentPath", request.getRequestURI());
    	return "complaint-register";  // this corresponds to complaint-register.html
    }

}
