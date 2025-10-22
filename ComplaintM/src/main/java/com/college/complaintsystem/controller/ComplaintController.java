package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.service.ComplaintService;
import java.util.List;

@RestController
@RequestMapping("/complaints")
@CrossOrigin("*") // allows frontend access
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping("/add")
    public Complaint addComplaint(@RequestBody Complaint complaint) {
        return complaintService.saveComplaint(complaint);
    }

    @GetMapping("/all")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @GetMapping("/{id}")
    public Complaint getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }
    
    @GetMapping("/student/{studentId}")
    public List<Complaint> getComplaintsByStudent(@PathVariable Long studentId) {
        return complaintService.getComplaintsByStudent(studentId);
    }
    
    @PutMapping("/{id}/status")
    public Complaint updateComplaintStatus(@PathVariable Long id, @RequestParam String status) {
        return complaintService.updateComplaintStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public String deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return "Complaint deleted successfully!";
    }
}
