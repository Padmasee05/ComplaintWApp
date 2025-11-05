package com.college.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.service.ComplaintService;
import java.util.List;
import java.util.Map;

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
    
    

    @DeleteMapping("/{id}")
    public String deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return "Complaint deleted successfully!";
    }
    
    // ✅ Assign complaint to any person (by name only)
    @PostMapping("/{id}/assign")
    public ResponseEntity<Map<String, Object>> assignComplaint(
            @PathVariable Long id,
            @RequestBody AssignRequest request) {

        Complaint updated = complaintService.assignComplaint(id, request.getAssigneeName());
        if (updated == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Invalid complaint or assignee name"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Complaint assigned successfully",
                "data", updated
        ));
    }

    // Update complaint status
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        Complaint updated = complaintService.updateComplaintStatus(id, request.getStatus());
        if (updated == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Complaint not found"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Complaint status updated",
                "data", updated
        ));
    }

    @GetMapping("/status/{status}")
    public List<Complaint> getComplaintsByStatus(@PathVariable String status) {
        return complaintService.getComplaintsByStatus(status);
    }

    // ==== Helper DTOs ====
    public static class AssignRequest {
        private String assigneeName;
        public String getAssigneeName() { return assigneeName; }
        public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
    }

    public static class StatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

}
