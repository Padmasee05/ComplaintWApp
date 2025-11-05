package com.college.complaintsystem.repository;

import com.college.complaintsystem.model.Feedback;
import com.college.complaintsystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStudent(Student student);
    Feedback findByComplaintId(Long complaintId);
}
