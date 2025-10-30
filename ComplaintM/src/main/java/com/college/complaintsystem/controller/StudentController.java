package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Student;
import com.college.complaintsystem.service.StudentService;
import java.util.List;

@Controller
@RequestMapping("/students")
@CrossOrigin("*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    @ResponseBody // this one can return JSON, it’s fine
    public Student addStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    //  Main Login method for web app
    @PostMapping("/login")
    public String loginStudent(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        Student student = studentService.login(email, password);

        if (student != null) {
            session.setAttribute("loggedInStudent", student);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password!");
            return "login";
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully!";
    }
}
