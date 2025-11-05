package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.model.Admin;
import com.college.complaintsystem.service.AdminService;
import com.college.complaintsystem.service.ComplaintService;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ComplaintService complaintService;

    // Dashboard page
    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(required = false) Long adminId, Model model) {
        Admin admin = adminService.getAdminById(adminId);

        if (admin == null) {
            return "redirect:/login"; // or show error page
        }

        model.addAttribute("admin", admin);
        String department = admin.getDepartment();
     // ✅ Stats
        Map<String, Long> stats = complaintService.getComplaintStatsByDepartment(department);
        model.addAttribute("stats", stats);

        // ✅ Urgent + Pending only
        List<Complaint> urgentComplaints = complaintService.findUrgentPendingComplaints(department);
        model.addAttribute("urgentComplaints", urgentComplaints);

        // ✅ Names list for assignment dropdown
        model.addAttribute("adminsList", adminService.getAllAdmins());
        return "admin-dashboard"; // maps to templates/admin-dashboard.html
    }

    // Navigation to resolve queue page
    // ✅ Resolve Queue Page
    @GetMapping("/resolve-queue")
    public String viewResolveQueue(@RequestParam Long adminId, Model model) {
        Admin admin = adminService.getAdminById(adminId);
        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute("admin", admin);
        String department = admin.getDepartment();

        model.addAttribute("pendingComplaints", complaintService.getComplaintsByStatusAndDept("PENDING", department));
        model.addAttribute("inProgressComplaints", complaintService.getComplaintsByStatusAndDept("IN_PROGRESS", department));
        model.addAttribute("resolvedComplaints", complaintService.getComplaintsByStatusAndDept("RESOLVED", department));

        return "resolve-queue"; // templates/admin/resolve-queue.html
    
    }
}
