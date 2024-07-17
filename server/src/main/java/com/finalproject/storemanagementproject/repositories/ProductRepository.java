package com.finalproject.storemanagementproject.repositories;

import com.finalproject.storemanagementproject.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByBarcode(String barcode);
    List<Product> findByNameContainingIgnoreCase(String name);
}
