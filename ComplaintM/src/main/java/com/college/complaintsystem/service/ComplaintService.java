package com.college.complaintsystem.service;

import java.util.List;
import java.util.stream.Collectors;
import com.college.complaintsystem.repository.AdminRepository;
import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;
import com.college.complaintsystem.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.repository.ComplaintRepository;
import java.time.LocalDateTime;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    public Complaint saveComplaint(Complaint complaint) {
        // Ensure stored status is normalized
        if (complaint.getStatus() != null) {
            complaint.setStatus(normalizeStatus(complaint.getStatus()));
        } else {
            complaint.setStatus("PENDING");
        }
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
    public Complaint updateComplaintStatus(Long id, String statusRaw) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
        	String status = normalizeStatus(statusRaw);
            complaint.setStatus(status);
            complaint.setUpdatedAt(LocalDateTime.now()); 
            return complaintRepository.save(complaint);
        }
        return null;
    }

    // ✅ Assign complaint by assignee name only
    public Complaint assignComplaint(Long complaintId, String assigneeName) {
        Complaint complaint = complaintRepository.findById(complaintId).orElse(null);
        if (complaint == null || assigneeName == null || assigneeName.isBlank()) {
            return null;
        }

        complaint.setAssignedTo(assigneeName.trim());
        complaint.setStatus("IN_PROGRESS");
        complaint.setUpdatedAt(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }
    
    public List<Complaint> getUrgentComplaints(String department) {
        return complaintRepository.findByCategoryAndUrgentTrue(department);
    }
    
 // Return only urgent AND pending complaints for a category
    public List<Complaint> findUrgentPendingComplaints(String category) {
        List<Complaint> complaints = complaintRepository.findByCategoryAndUrgentTrue(category);
        System.out.println("✅ Urgent complaints fetched for category " + category + ": " + complaints.size());
        
        complaints = complaints.stream()
                .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus()))
                .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                .collect(Collectors.toList());
        
        System.out.println("✅ Urgent & Pending complaints after filtering: " + complaints.size());
        return complaints;
    }

    public List<Complaint> getComplaintsByDepartment(String category) {
        return complaintRepository.findByCategory(category);
    }

    public Map<String, Long> getComplaintStatsByDepartment(String category) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", complaintRepository.countByCategory(category));
        stats.put("pending", complaintRepository.countByCategoryAndStatus(category, "PENDING"));
        stats.put("resolved", complaintRepository.countByCategoryAndStatus(category, "RESOLVED"));
        // count urgent & pending
        long urgentPending = complaintRepository.findByCategoryAndUrgentTrue(category)
                .stream()
                .filter(c -> "PENDING".equalsIgnoreCase(c.getStatus()))
                .count();
        stats.put("urgent", urgentPending);
        return stats;
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
    
 //  Get complaints by status
    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status);
    }


    
 //  get frequent complaints of the last week
    public List<Object[]> findFrequentComplaintsLastWeek(LocalDateTime oneWeekAgo) {
        return complaintRepository.findFrequentComplaintsLastWeek(oneWeekAgo);
    }
    
    private String normalizeStatus(String raw) {
        if (raw == null) return "PENDING";
        String s = raw.trim().toUpperCase().replaceAll("\\s+", "_");
        // Map common variations to allowed values
        if (s.equals("PENDING") || s.equals("IN_PROGRESS") || s.equals("RESOLVED")) {
            return s;
        }
        // Some frontends may pass "INPROGRESS" or "IN-PROGRESS"
        if (s.contains("IN") && s.contains("PROGR")) return "IN_PROGRESS";
        if (s.contains("RESOLV")) return "RESOLVED";
        return "PENDING";
    }
}
