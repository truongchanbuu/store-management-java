package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.Role;
import com.finalproject.storemanagementproject.models.Status;
import com.finalproject.storemanagementproject.models.User;
import com.finalproject.storemanagementproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Iterable<User> searchUser(String text) {
        return userRepository.findByUsernameContaining(text);
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) return false;

        userRepository.save(user);
        return true;
    }

    public boolean updateUser(User user) {
        if (userRepository.findById(user.getId()).orElse(null) == null) return false;

        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(String id) {
        if (userRepository.findById(id).orElse(null) == null) return false;

        userRepository.deleteById(id);
        return true;
    }

    public boolean isValidRole(String role) {
        for (Role r : Role.values())
            if (r.name().equals(role))
                return true;

        return false;
    }

    public boolean isValidStatus(String status) {
        for (Status s : Status.values())
            if (s.name().equals(status))
                return true;

        return false;
    }
    
    public long getTotalUser() {
    	return userRepository.count();
    }
}
