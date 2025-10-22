package com.college.complaintsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.complaintsystem.model.Complaint;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
	// Find all complaints of a specific student
    List<Complaint> findByStudentId(Long studentId);

    // Find urgent complaints
    List<Complaint> findByUrgentTrue();

    // Find complaints by status (e.g., "Pending", "Resolved")
    List<Complaint> findByStatus(String status);
}
