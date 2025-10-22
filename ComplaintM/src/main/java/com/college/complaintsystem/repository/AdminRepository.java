package com.college.complaintsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.college.complaintsystem.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	 Admin findByEmailAndPassword(String email, String password);
}
