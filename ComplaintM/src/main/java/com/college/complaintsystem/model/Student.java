package com.college.complaintsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name: cannot contain numbers or special chars
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name must not contain numbers or special characters")
    @Column(nullable = false)
    private String name;

    //  Email: must be valid & unique
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Column(unique = true, nullable = false)
    private String email;

    // Roll Number: exactly 9 characters & unique
    @NotBlank(message = "Roll number is required")
    @Size(min = 9, max = 9, message = "Roll number must be exactly 9 characters long")
    @Column(unique = true, nullable = false)
    private String rollNumber;

    // Phone Number: exactly 10 digits
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    //  Department: cannot be empty
    @NotBlank(message = "Department is required")
    private String department;

    //  Hostel: must contain 'Block' or 'NA'
    @NotBlank(message = "Hostel information is required")
    @Pattern(regexp = "^(?i)(.*block.*|NA)$", message = "Hostel must contain 'Block' or be 'NA'")
    private String hostel;

    //  Password: 8+ chars, uppercase, lowercase, digit, special char
    @NotBlank(message = "Password cannot be empty")
    private String password;

    public Student() {
    }

    public Student(String name, String email, String rollNumber, String phoneNumber,
            String department, String hostel, String password) {
		 this.name = name;
		 this.email = email;
		 this.rollNumber = rollNumber;
		 this.phoneNumber = phoneNumber;
		 this.department = department;
		 this.hostel = hostel;
		 this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
