package com.college.complaintsystem.service;

import java.util.List;
import java.util.Comparator;
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

    public List<Complaint> getComplaintsByStudentId(Long studentId) {
        return complaintRepository.findByStudentId(studentId);
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
            complaint.setUpdatedAt(LocalDateTime.now()); 
            return complaintRepository.save(complaint);
        }
        return null;
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
    
    public List<Complaint> getRecentComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId)
                .stream()
                .sorted(Comparator.comparing(Complaint::getUpdatedAt).reversed())
                .limit(5)
                .toList();
    }

    
 //  get frequent complaints of the last week
    public List<Object[]> findFrequentComplaintsLastWeek(LocalDateTime oneWeekAgo) {
        return complaintRepository.findFrequentComplaintsLastWeek(oneWeekAgo);
    }
}
