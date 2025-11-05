package com.college.complaintsystem.service;

import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.college.complaintsystem.model.Student;
import com.college.complaintsystem.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public Student saveStudent(Student student) {
    	
    	 Student existingStudent = studentRepository.findByEmail(student.getEmail());
    	    if (existingStudent != null && !existingStudent.getId().equals(student.getId())) {
    	        throw new IllegalArgumentException("Email already registered. Please use another email.");
    	    }
        
        // – Validate password format 
        String password = student.getPassword();
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

        if (!password.matches(pattern)) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character."
            );
        }
       

        // – Encode password only if not already encoded
        if (!password.startsWith("$2a$")) {
            student.setPassword(passwordEncoder.encode(password));
        }

        //  – Save to DB
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }
    
    public Student login(String email, String password) {
    	 Student student = studentRepository.findByEmail(email);
         if (student != null && passwordEncoder.matches(password, student.getPassword())) {
             return student;
         }
         return null;
         
    }


    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    

}
