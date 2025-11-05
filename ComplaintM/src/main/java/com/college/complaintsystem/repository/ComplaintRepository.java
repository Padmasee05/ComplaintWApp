package com.college.complaintsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.complaintsystem.model.Complaint;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import org.springframework.data.repository.query.Param;


public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
	// Find all complaints of a specific student
    List<Complaint> findByStudentId(Long studentId);

    // Find urgent complaints
    List<Complaint> findByUrgentTrue();

    // Find complaints by status (e.g., "Pending", "Resolved")
    List<Complaint> findByStatus(String status);
  
    long countByCategory(String category);

    long countByCategoryAndStatus(String category, String status);

    //  use urgent flag
    long countByCategoryAndUrgentTrue(String category);

    List<Complaint> findByCategory(String category);

    List<Complaint> findByCategoryAndUrgentTrue(String category);
    
    List<Complaint> findByCategoryAndStatus(String category, String status);

    
 //  Find most frequent complaints from the last 7 days
    @Query("SELECT c.category, COUNT(c) as count " +
    	       "FROM Complaint c " +
    	       "WHERE c.createdAt >= :oneWeekAgo " +
    	       "GROUP BY c.category " +
    	       "ORDER BY COUNT(c) DESC")
    	List<Object[]> findFrequentComplaintsLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo);
}
