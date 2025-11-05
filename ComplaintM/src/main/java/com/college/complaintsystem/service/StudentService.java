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
    	student.setPassword(passwordEncoder.encode(student.getPassword()));
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
