package com.college.complaintsystem.controller;

import com.college.complaintsystem.model.Student;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;  
import com.college.complaintsystem.model.Complaint;
import com.college.complaintsystem.service.ComplaintService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.college.complaintsystem.service.StudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class DashboardController {

    @Autowired
    private ComplaintService complaintService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, HttpServletRequest request, Model model) {
        // get logged-in student from session
        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            return "redirect:/login";
        }
        
        

        // Fetch user-specific data
        List<Complaint> userComplaints = complaintService.getComplaintsByStudentId(student.getId());
     // Show only top 2 most recent complaints on dashboard
        List<Complaint> recentThreeComplaints = userComplaints.stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(2)
                .toList();
        long pendingCount   = userComplaints.stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count();
        long resolvedCount  = userComplaints.stream().filter(c -> "Resolved".equalsIgnoreCase(c.getStatus())).count();

     // ✅ Frequent complaints from the last 7 days (this week)
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> weeklyFrequentData = complaintService.findFrequentComplaintsLastWeek(oneWeekAgo);

     // Convert to readable structure for the view (category + count)
        List<Map<String, Object>> frequentComplaints = weeklyFrequentData.stream()
            .map(obj -> {
                Map<String, Object> map = new HashMap<>();
                map.put("category", (String) obj[0]);
                map.put("count", ((Long) obj[1]));
                return map;
            })
            .limit(3) // only top 3
            .toList();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        List<String> recentActivity = userComplaints.stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(5)
                .map(c -> "Complaint #" + c.getId() + " (" + c.getCategory() + ") - " +
                        c.getStatus() + " • " + c.getUpdatedAt().format(formatter))
                .toList();
        	


        // Add data to model
        model.addAttribute("recentActivity", recentActivity);
        model.addAttribute("student", student);
        model.addAttribute("userComplaints", userComplaints);
        model.addAttribute("recentThreeComplaints", recentThreeComplaints); // 3 only (for dashboard)
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("resolvedCount", resolvedCount);
        model.addAttribute("frequentComplaints", frequentComplaints);
        model.addAttribute("totalComplaintsCount", userComplaints.size());
        model.addAttribute("currentPath", request.getRequestURI());
        return "dashboard";
    }
    
    @GetMapping("/search")
    public String searchComplaints(String keyword,HttpSession session,HttpServletRequest request, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        List<Complaint> allComplaints = complaintService.getComplaintsByStudentId(student.getId());

        // Filter complaints based on keyword
        List<Complaint> filteredComplaints = allComplaints.stream()
                .filter(c ->
                        c.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        c.getCategory().toLowerCase().contains(keyword.toLowerCase()) ||
                        c.getStatus().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();

        model.addAttribute("userComplaints", filteredComplaints);
        model.addAttribute("student", student);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pendingCount", filteredComplaints.stream().filter(c -> "Pending".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("resolvedCount", filteredComplaints.stream().filter(c -> "Resolved".equalsIgnoreCase(c.getStatus())).count());
        model.addAttribute("currentPath", request.getRequestURI());

        return "dashboard";
    }
    
    @GetMapping("/complaint/register")
    public String showComplaintRegisterPage(
            @RequestParam(required = false) String category,
            HttpSession session,
            HttpServletRequest request,
            Model model) {

        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("selectedCategory", category);

        return "complaint-register";
    }



    
    @PostMapping("/complaint/submit")
    public String submitComplaint(
            @RequestParam("category") String category,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "anonymous", required = false) boolean anonymous,
            @RequestParam(value = "urgent", required = false) boolean urgent,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session,
            Model model
    ) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        Complaint complaint = new Complaint();
        complaint.setCategory(category);
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setAnonymous(anonymous);
        complaint.setUrgent(urgent);
        complaint.setStatus("Pending");
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());

        if (!anonymous) {
            complaint.setAnonymous(false);
        } else {
            complaint.setAnonymous(true);
        }
        complaint.setStudent(student);


        // ✅ Handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                // Create upload folder if it doesn’t exist
                String uploadDir = "uploads/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate unique file name
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                // Save file to disk
                Files.write(filePath, file.getBytes());

                // Save relative path in DB
                complaint.setImageUrl("/" + uploadDir + fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ✅ Save complaint in database
        complaintService.saveComplaint(complaint);

        // Redirect with success message
        model.addAttribute("successMessage", "Complaint submitted successfully!");
        return "complaint-register";
    }
    
    @GetMapping("/complaint/track")
    public String showTrackComplaintPage(HttpSession session, HttpServletRequest request, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        // Fetch all complaints of the logged-in student
        List<Complaint> userComplaints = complaintService.getComplaintsByStudentId(student.getId());

        model.addAttribute("userComplaints", userComplaints);
        model.addAttribute("student", student);
        model.addAttribute("currentPath", request.getRequestURI());

        return "track-complaint"; // corresponds to track-complaint.html
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, HttpServletRequest request, Model model) {
        Student student = (Student) session.getAttribute("loggedInStudent");
        if (student == null) {
            return "redirect:/login";
        }

        model.addAttribute("student", student);
        model.addAttribute("currentPath", request.getRequestURI());
        return "profile";
    }

    @PostMapping("/change-password")
    @ResponseBody
    public Map<String, String> changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            HttpSession session
    ) {
        Map<String, String> response = new HashMap<>();
        Student student = (Student) session.getAttribute("loggedInStudent");

        if (student == null) {
            response.put("status", "error");
            response.put("message", "Session expired. Please log in again.");
            return response;
        }

        // ✅ Verify old password
        if (!passwordEncoder.matches(currentPassword, student.getPassword())) {
            response.put("status", "error");
            response.put("message", "Current password is incorrect!");
            return response;
        }

        // ✅ Validate new password strength before saving
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            response.put("status", "error");
            response.put("message", "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
            return response;
        }

        // ✅ Encrypt and save new password
        student.setPassword(passwordEncoder.encode(newPassword));
        studentService.saveStudent(student);
        session.setAttribute("loggedInStudent", student);

        response.put("status", "success");
        response.put("message", "Password changed successfully!");
        return response;
    }



}
