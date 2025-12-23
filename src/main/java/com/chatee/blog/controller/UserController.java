package com.chatee.blog.controller;

import com.chatee.blog.dto.LoginRequest;
import com.chatee.blog.dto.ApiResponse;
import com.chatee.blog.entities.User;
import com.chatee.blog.service.UserService;
import com.chatee.blog.service.EmailService; // ✅ 1. Import EmailService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // ✅ Security Import
import org.springframework.web.bind.annotation.*;

import java.util.Random; // ✅ Import Random for OTP

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService; // ✅ 2. Inject EmailService

    // Security: Encrypt passwords so they aren't plain text
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // --- LOGIN API ---
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.loadUserByUsername(request.getUsername());

            // Check 1: User exists?
            // Check 2: Password matches? (using BCrypt for security)
            // Check 3: Is the account verified? (enabled == true)
            if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {

                if (!user.isEnabled()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new ApiResponse("Account not verified. Please check your email for OTP.", null));
                }

                // Return safe user object
                User safeUser = new User();
                safeUser.setId(user.getId());
                safeUser.setUsername(user.getUsername());
                safeUser.setName(user.getName());
                safeUser.setMobile(user.getMobile());
                safeUser.setEmail(user.getEmail());
                safeUser.setRole(user.getRole());

                return ResponseEntity.ok(new ApiResponse("Login successful", safeUser));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid username or password", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Server error: " + e.getMessage(), null));
        }
    }

    // --- REGISTER API (With Email Sending) ---
    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User user) {
        try {
            // Check if user already exists
            User existing = userService.loadUserByUsername(user.getUsername());
            if (existing != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("Username already exists", null));
            }

            // ✅ 3. Generate 6-Digit OTP
            String otp = String.valueOf(new Random().nextInt(900000) + 100000);
            user.setVerificationCode(otp);
            user.setEnabled(false); // User is locked until they verify

            // ✅ 4. Hash the password (Security)
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save to Database
            User savedUser = userService.saveUser(user);

            // ✅ 5. SEND EMAIL
            System.out.println("Sending OTP to: " + user.getEmail()); // Debug print
            emailService.sendVerificationEmail(user.getEmail(), otp);

            // Hide sensitive data in response
            savedUser.setPassword(null);
            savedUser.setVerificationCode(null);

            return ResponseEntity.ok(new ApiResponse("Registration successful. OTP sent to email.", savedUser));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Server error: " + e.getMessage(), null));
        }
    }

    // --- VERIFY OTP API ---
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(@RequestBody LoginRequest request) {
        try {
            // We use LoginRequest here: username = username, password = OTP code
            User user = userService.loadUserByUsername(request.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User not found", null));
            }

            // Check if the OTP matches
            if (user.getVerificationCode().equals(request.getPassword())) {
                user.setEnabled(true); // ✅ Activate Account
                user.setVerificationCode(null); // Clear OTP
                userService.saveUser(user);

                return ResponseEntity.ok(new ApiResponse("Account verified successfully!", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse("Invalid OTP Code", null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }

    // --- OTHER API METHODS ---
    @GetMapping("/login")
    public ResponseEntity<ApiResponse> loginGetNotSupported() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ApiResponse("Use POST to login", null));
    }

    @GetMapping("/userById")
    public ResponseEntity<ApiResponse> getUserById(@RequestParam Long id) {
        User user = userService.loadUserById(id);
        return (user != null) ? ResponseEntity.ok(new ApiResponse("Found", user))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not Found", null));
    }

    @PutMapping("/updateUserName")
    public ResponseEntity<ApiResponse> updateUserName(@RequestBody User user) {
        User existingUser = userService.loadUserById(user.getId());
        if (existingUser == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Not found", null));

        existingUser.setName(user.getName());
        return ResponseEntity.ok(new ApiResponse("Updated", userService.saveUser(existingUser)));
    }
}