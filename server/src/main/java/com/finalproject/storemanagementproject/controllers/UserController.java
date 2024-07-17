package com.finalproject.storemanagementproject.controllers;

import com.finalproject.storemanagementproject.middleware.MailService;
import com.finalproject.storemanagementproject.middleware.PasswordService;
import com.finalproject.storemanagementproject.middleware.ResetPasswordTokenService;
import com.finalproject.storemanagementproject.models.APIResponse;
import com.finalproject.storemanagementproject.models.Role;
import com.finalproject.storemanagementproject.models.Status;
import com.finalproject.storemanagementproject.models.User;
import com.finalproject.storemanagementproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordService passwordService;
    private final MailService mailService;
    private final ResetPasswordTokenService rPTService;

    @Value("${default.avatar.url}")
    private String defaultAvatarUrl;

    @Autowired
    public UserController(UserService userService,
                          MailService mailService,
                          PasswordService passwordService,
                          ResetPasswordTokenService rPTService) {
        this.rPTService = rPTService;
        this.mailService = mailService;
        this.userService = userService;
        this.passwordService = passwordService;
    }

    @GetMapping(value = "/admin/users", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(required = false) String text, @RequestParam(required = false) String email) {
        Iterable<User> users = null;
        if (text != null && !text.isEmpty()) users = userService.searchUser(text);
        else if (email != null && !email.isEmpty())
            users = Collections.singletonList(userService.getUserByEmail(email));
        else users = userService.getAllUsers();

        for (User user : users) user.setPassword("");

        return ResponseEntity.ok(Map.of("message", "Get users success", "users", users));
    }

    @GetMapping(value = "/admin/users/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));

        user.setPassword("");

        return ResponseEntity.ok(Map.of("message", "Get user success", "user", user));
    }

    @PostMapping(value = "/admin/users/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Please fill all fields"));

        String domain = mailService.HOST.split("smtp.")[1];
        if (!email.matches("^\\w+@(" + domain + ")$"))
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or domain"));

        if (userService.getUserByEmail(email) != null)
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));

        String username = email.split("@")[0];
        String password = passwordService.hashPassword(username);

        User user = new User(null, email, username, password, Status.NORMAL, Role.EMPLOYEE, defaultAvatarUrl);

        boolean isAdded = userService.addUser(user);

        if (!isAdded)
            return ResponseEntity.badRequest().body(Map.of("message", "Create user failed"));

        ResponseEntity<Map<String, Object>> response = resetPassword(user.getId());
        if (response.getStatusCode().isError())
            return response;

        return ResponseEntity.ok(Map.of("message", "Create user success. A reset password mail has been sent to user", "user", user));
    }

    @PostMapping(value = "/admin/users/update/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody Map<String, String> body) {
        String role = body.get("role").toUpperCase();
        String status = body.get("status").toUpperCase();

        if (role.isEmpty() || status.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Please fill all fields"));

        if (!userService.isValidRole(role))
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid role"));

        if (!userService.isValidStatus(status))
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status"));

        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));

        if (user.getRole().equals(Role.OWNER) && !status.equals(Status.LOCKED.toString()))
            return ResponseEntity.badRequest().body(Map.of("message", "Can not lock owner"));

        user.setRole(Role.valueOf(role));
        user.setStatus(Status.valueOf(status));

        boolean isUpdated = userService.updateUser(user);
        if (!isUpdated)
            return ResponseEntity.badRequest().body(Map.of("message", "Update user failed"));

        return ResponseEntity.ok(Map.of("message", "Update user success", "user", user));
    }

    @PostMapping(value = "/admin/users/delete/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));

        if (user.getRole().equals(Role.OWNER))
            return ResponseEntity.badRequest().body(Map.of("message", "Can not delete owner"));

        boolean isDeleted = userService.deleteUser(id);
        if (!isDeleted)
            return ResponseEntity.badRequest().body(Map.of("message", "Delete user failed"));

        return ResponseEntity.ok(Map.of("message", "Delete user success", "user", user));
    }

    @GetMapping(value = "/admin/users/reset-password/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user == null)
            return ResponseEntity.badRequest().body(Map.of("message", "User not found"));

        String token = rPTService.createToken(id);
        if (token == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Create token failed"));

        if (!mailService.sendResetPasswordMail(user.getEmail(), token))
            return ResponseEntity.badRequest().body(Map.of("message", "Send reset password mail failed"));

        return ResponseEntity.ok(Map.of("message", "Send reset password mail success", "user", user));
    }

    @PostMapping(value = "/users/change-avatar/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> changeAvatar(@PathVariable String id, @RequestBody Map<String, String> body) {
        String avatarUrl = body.get("avatarUrl");
        User user = userService.getUserById(id);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("message", "User not found"));

        if (!avatarUrl.matches("^https?://.*\\.(?:png|jpg|jpeg|gif)$"))
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid avatar url"));

        user.setAvatar(avatarUrl);
        boolean isUpdated = userService.updateUser(user);
        if (!isUpdated) return ResponseEntity.badRequest().body(Map.of("message", "Change avatar failed"));

        return ResponseEntity.ok(Map.of("message", "Change avatar success", "user", user));
    }

    @PostMapping(value = "/users/change-password/{id}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable String id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");

        User user = userService.getUserById(id);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("message", "User not found"));

        if (newPassword == null || newPassword.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));

        if (!newPassword.equals(confirmPassword))
            return ResponseEntity.badRequest().body(Map.of("message", "Confirm password not match"));

        if (newPassword.equals(user.getUsername()))
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be different from username"));

        newPassword = passwordService.hashPassword(newPassword);
        user.setPassword(newPassword);
        boolean isUpdated = userService.updateUser(user);
        if (!isUpdated) return ResponseEntity.badRequest().body(Map.of("message", "Change password failed"));

        return ResponseEntity.ok().body(Map.of("message", "Change password success", "user", user));
    }

    // Get total user
    @GetMapping("/users/total")
    public ResponseEntity<APIResponse<Long>> getTotalUser() {
    	long total = userService.getTotalUser();
    	return ResponseEntity.ok(new APIResponse<Long>(HttpStatus.OK.value(), "Success", Collections.singletonList(total)));
    }
}
