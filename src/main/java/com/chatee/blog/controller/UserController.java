package com.chatee.blog.controller;

import com.chatee.blog.dto.LoginRequest;
import com.chatee.blog.dto.ApiResponse;
import com.chatee.blog.entities.User;
import com.chatee.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // Login API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.loadUserByUsername(request.getUsername());
            if (user != null && user.getPassword().equals(request.getPassword())) {
                // Create a new user object without password for security
                User safeUser = new User();
                safeUser.setId(user.getId());
                safeUser.setUsername(user.getUsername());
                safeUser.setName(user.getName());
                safeUser.setMobile(user.getMobile());
                safeUser.setRole(user.getRole());

                return ResponseEntity.ok(new ApiResponse("Login successful", safeUser));
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Invalid username or password", null));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Server error: " + e.getMessage(), null));
        }
    }

    // Handle GET /login gracefully
    @GetMapping("/login")
    public ResponseEntity<ApiResponse> loginGetNotSupported() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiResponse("Please use POST method to login", null));
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String helloGFG() {
        return "Hello GeeksForGeeks";
    }

    @GetMapping("/userById")
    public ResponseEntity<ApiResponse> getUserById(@RequestParam Long id) {
        try {
            User user = userService.loadUserById(id);
            if (user != null) {
                return ResponseEntity.ok(new ApiResponse("User found", user));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Server error: " + e.getMessage(), null));
        }
    }


    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody User user) {
        try {
            System.out.println("Incoming user: " + user);
            User existing = userService.loadUserByUsername(user.getUsername());
            System.out.println("Existing user: " + existing);
            if (existing != null) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("Username already exists", null));
            }

            User savedUser = userService.saveUser(user);
            savedUser.setPassword(null);

            return ResponseEntity.ok(new ApiResponse("Registration successful", savedUser));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Server error: " + e.getMessage(), null));
        }
    }

    @PutMapping("/updateUserName")
    public ResponseEntity<ApiResponse> updateUserName(@RequestBody User user) {
        try {
            User existingUser = userService.loadUserById(user.getId());
            if (existingUser == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User not found", null));
            }
            existingUser.setName(user.getName());
            User updatedUser = userService.saveUser(existingUser);
            return ResponseEntity.ok(new ApiResponse("Name updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Server error: " + e.getMessage(), null));
        }
    }
}