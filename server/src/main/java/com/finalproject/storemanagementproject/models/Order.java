package com.finalproject.storemanagementproject.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
@Getter
@Setter
@Data
public class Order {
    @Id
    private String oid;
    private Customer customer;
    private User user;
    private double totalPrice;
    private Status orderStatus;
    private List<OrderProduct> orderProducts;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
