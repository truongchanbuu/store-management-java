package com.finalproject.storemanagementproject.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order_products")
@Data
public class OrderProduct {
    @Id
    private String id;
    private String pid;
    private String oid;
    private int quantity;
    private double importPrice;
    private double retailPrice;
}
