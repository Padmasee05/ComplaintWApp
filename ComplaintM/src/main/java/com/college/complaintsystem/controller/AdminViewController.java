package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.model.Feedback;
import com.college.complaintsystem.model.Admin;
import com.college.complaintsystem.service.AdminService;
import com.college.complaintsystem.service.ComplaintService;
import com.college.complaintsystem.service.FeedbackService;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private FeedbackService feedbackService;

    
 // Show login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin-login";
    }

    // Handle login POST
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              Model model,
                              HttpSession session) {
        Admin admin = adminService.login(email, password);

        if (admin == null) {
            model.addAttribute("error", "Invalid email or password");
            return "admin-login";
        }

        // ✅ Save admin in session
        session.setAttribute("admin", admin);
        session.setAttribute("role", "ADMIN"); // ✅ add role-based flag

        return "redirect:/admin/dashboard";
    }

    
    // Dashboard page
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        String role = (String) session.getAttribute("role");

        if (admin == null || !"ADMIN".equals(role)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("admin", admin);
        String department = admin.getDepartment();

        Map<String, Long> stats = complaintService.getComplaintStatsByDepartment(department);
        model.addAttribute("stats", stats);

        List<Complaint> urgentComplaints = complaintService.findUrgentPendingComplaints(department);
        model.addAttribute("urgentComplaints", urgentComplaints);

        model.addAttribute("adminsList", adminService.getAllAdmins());
        return "admin-dashboard";
    }


    //  Resolve Queue Page
    @GetMapping("/resolve-queue")
    public String viewResolveQueue(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        String role = (String) session.getAttribute("role");

        if (admin == null || !"ADMIN".equals(role)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("admin", admin);
        String department = admin.getDepartment();

        model.addAttribute("pendingComplaints", complaintService.getComplaintsByStatusAndDept("PENDING", department));
        model.addAttribute("inProgressComplaints", complaintService.getComplaintsByStatusAndDept("IN_PROGRESS", department));
        model.addAttribute("resolvedComplaints", complaintService.getComplaintsByStatusAndDept("RESOLVED", department));

        return "resolve-queue";
    }

    @GetMapping("/feedbacks")
    public String viewFeedbacks(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        String role = (String) session.getAttribute("role");

        if (admin == null || !"ADMIN".equals(role)) {
            return "redirect:/admin/login";
        }

        String department = admin.getDepartment();

        // Fetch feedbacks whose complaint belongs to this department
        List<Feedback> feedbackList = feedbackService.getFeedbacksByDepartment(department);

        model.addAttribute("admin", admin);
        model.addAttribute("feedbacks", feedbackList);
        return "admin-feedbacks";
    }

    
    // Logout Route
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); //  Clears session
        return "redirect:/admin/login?logout=true";
    }
}
