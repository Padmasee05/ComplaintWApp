package com.college.complaintsystem.controller;

import com.college.complaintsystem.model.*;
import com.college.complaintsystem.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FeedbackController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedback")
    public String showFeedbackPage(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) return "redirect:/login";

        // Get only resolved complaints that don’t yet have feedback
        List<Complaint> resolvedComplaints = complaintService.getComplaintsByStudentId(student.getId())
                .stream()
                .filter(c -> "Resolved".equalsIgnoreCase(c.getStatus()) && feedbackService.getByComplaintId(c.getId()) == null)
                .toList();

        List<Feedback> feedbackHistory = feedbackService.getFeedbackByStudent(student);

        model.addAttribute("resolvedComplaints", resolvedComplaints);
        model.addAttribute("feedbackHistory", feedbackHistory);
        model.addAttribute("student", student);
        return "feedback";
    }

    @PostMapping("/feedback/submit")
    @ResponseBody
    public String submitFeedback(
            @RequestParam("complaintId") Long complaintId,
            @RequestParam("rating") int rating,
            @RequestParam(value = "comment", required = false) String comment,
            HttpSession session) {

        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) return "{\"status\":\"error\",\"message\":\"Login required\"}";

        feedbackService.saveFeedback(student, complaintId, rating, comment);
        return "{\"status\":\"success\",\"message\":\"Feedback submitted successfully!\"}";
    }

}
