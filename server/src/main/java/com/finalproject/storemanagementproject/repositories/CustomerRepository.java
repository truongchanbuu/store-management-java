package com.finalproject.storemanagementproject.repositories;

import com.finalproject.storemanagementproject.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findByPhone(String phone);

    List<Customer> findByNameContainingIgnoreCase(String name);
}
