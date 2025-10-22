package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Student;
import com.college.complaintsystem.service.StudentService;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/students")
@CrossOrigin("*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    public Student addStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @PostMapping("/login")
    public ResponseEntity<Student> loginStudent(@RequestParam String email,
                                                @RequestParam String password) {
        Student student = studentService.login(email, password);

        if (student != null) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/all")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully!";
    }
}
