package com.finalproject.storemanagementproject.repositories;

import com.finalproject.storemanagementproject.models.ResetPasswordToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordTokenRepository extends MongoRepository<ResetPasswordToken, String> {

}
