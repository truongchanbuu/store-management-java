package com.finalproject.storemanagementproject.repositories;

import com.finalproject.storemanagementproject.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    List<User> findByUsernameContaining(String username);

    User findByEmail(String email);
}
