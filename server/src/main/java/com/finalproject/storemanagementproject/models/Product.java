package com.finalproject.storemanagementproject.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "products")
@Data
@Getter
@Setter
public class Product {
    @Id
    private String pid;
    private String name;
    private Category category;
    private double importPrice;
    private double retailPrice;
    @Indexed(unique = true)
    private String barcode;
    private String illustrator;
    private int quantity;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
