package com.finalproject.storemanagementproject.controllers;

import com.finalproject.storemanagementproject.middleware.JWTTokenService;
import com.finalproject.storemanagementproject.middleware.PasswordService;
import com.finalproject.storemanagementproject.middleware.ResetPasswordTokenService;
import com.finalproject.storemanagementproject.models.User;
import com.finalproject.storemanagementproject.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public static final String ADMIN_RESOURCE = "ADMIN";
    public static final String OWNER_RESOURCE = "OWNER";
    public static final HttpStatus FORBIDDEN = HttpStatus.FORBIDDEN;
    public static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    public static final String NO_PERMISSION_MESSAGE = "You don't have permission to access this resource";

    private final UserService userService;
    private final JWTTokenService JWTTokenService;
    private final PasswordService passwordService;
    private final ResetPasswordTokenService rPTService;

    @Autowired
    public AuthController(UserService userService,
                          JWTTokenService JWTTokenService,
                          PasswordService passwordService,
                          ResetPasswordTokenService rPTService) {
        this.rPTService = rPTService;
        this.userService = userService;
        this.JWTTokenService = JWTTokenService;
        this.passwordService = passwordService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userService.getUserByUsername(username);
        if (user == null ||
                !passwordService.checkPassword(password, user.getPassword())
        )
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials!"));

        if (user.getStatus().toString().equals("LOCKED"))
            return ResponseEntity.badRequest().body(Map.of("message", "Please contact admin to unlock your account!"));

        if (user.getUsername().equals(password))
            return ResponseEntity.badRequest().body(Map.of("message", "Please contact admin to reset your password!"));

        String token = JWTTokenService.generateToken(user);
        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(Map.of("message", "Login success", "user", user));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam(required = false) String token, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");

        if (token == null || token.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid token"));

        String userId = rPTService.getUserIdFromToken(token);
        if (userId == null || userId.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid token"));

        User user = userService.getUserById(userId);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid token"));

        if (newPassword == null || newPassword.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Password not found"));

        if (!newPassword.equals(confirmPassword))
            return ResponseEntity.badRequest().body(Map.of("message", "Password and confirm password not match"));

        if (newPassword.equals(user.getUsername()))
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be different from username"));

        String hashedPassword = passwordService.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        boolean isUpdated = userService.updateUser(user);
        if (!isUpdated) return ResponseEntity.badRequest().body(Map.of("message", "Reset password failed"));

        return ResponseEntity.ok(Map.of("message", "Reset password successfully. Please login again!", "user", user));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String token,
                                                        @RequestBody Map<String, String> body,
                                                        HttpServletRequest request) {
        String resource = body.get("resource");

        token = token.substring(7);
        String email = JWTTokenService.validateToken(token);
        User user = userService.getUserByEmail(email);

        if (user == null) return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));

        if (resource.equalsIgnoreCase(ADMIN_RESOURCE)) {
            if (!user.getRole().toString().equalsIgnoreCase("ADMIN") && !user.getRole().toString().equalsIgnoreCase("OWNER")) {
                return ResponseEntity.status(FORBIDDEN).body(Map.of("message", NO_PERMISSION_MESSAGE));
            }
        } else if (resource.equalsIgnoreCase(OWNER_RESOURCE)) {
            if (!user.getRole().toString().equalsIgnoreCase("OWNER")) {
                return ResponseEntity.status(FORBIDDEN).body(Map.of("message", NO_PERMISSION_MESSAGE));
            }
        }

        return ResponseEntity.ok(Map.of("message", "Valid token", "user", user));
    }

}
