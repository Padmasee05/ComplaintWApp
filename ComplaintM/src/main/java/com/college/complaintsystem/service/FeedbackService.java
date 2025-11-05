package com.college.complaintsystem.service;

import com.college.complaintsystem.model.*;
import com.college.complaintsystem.repository.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ComplaintService complaintService;

    public void saveFeedback(Student student, Long complaintId, int rating, String comment) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint == null) return;

        Feedback feedback = new Feedback();
        feedback.setComplaint(complaint);
        feedback.setStudent(student);
        feedback.setRating(rating);
        feedback.setComment(comment);

        feedbackRepository.save(feedback);
    }

    public Feedback getByComplaintId(Long complaintId) {
        return feedbackRepository.findByComplaintId(complaintId);
    }
    
    public List<Feedback> getFeedbackByStudent(Student student) {
        return feedbackRepository.findByStudent(student);
    }

    public List<Feedback> getFeedbacksByDepartment(String department) {
        return feedbackRepository.findAll().stream()
                .filter(f -> f.getComplaint() != null &&
                             f.getComplaint().getCategory() != null &&
                             f.getComplaint().getCategory().equalsIgnoreCase(department))
                .toList();
    }

}
