package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Admin;
import com.college.complaintsystem.service.AdminService;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/admins")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @PostMapping("/register")
    public Admin registerAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }

    @GetMapping("/login")
    public ResponseEntity<Admin> loginAdmin(@RequestParam String email, @RequestParam String password) {
        Admin admin = adminService.login(email, password);
        if (admin != null) {
            return ResponseEntity.ok(admin);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PostMapping("/add")
    public Admin addAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }

    @GetMapping("/all")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{id}")
    public Admin getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return "Admin deleted successfully!";
    }
}
