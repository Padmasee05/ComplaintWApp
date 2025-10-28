package com.college.complaintsystem.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.repository.ComplaintRepository;
import java.time.LocalDateTime;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }
    
 // Get complaints by student
    public List<Complaint> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId);
    }
    
    // Update complaint status (for admin or tracking)
    public Complaint updateComplaintStatus(Long id, String status) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setStatus(status);
            return complaintRepository.save(complaint);
        }
        return null;
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
    
 // ✅ New method — get frequent complaints of the last week
    public List<Object[]> getFrequentComplaintsLastWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return complaintRepository.findFrequentComplaintsLastWeek(oneWeekAgo);
    }
}
