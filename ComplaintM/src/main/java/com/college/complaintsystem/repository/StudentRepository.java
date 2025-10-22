package com.college.complaintsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.complaintsystem.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Student findByEmailAndPassword(String email, String password);

}
