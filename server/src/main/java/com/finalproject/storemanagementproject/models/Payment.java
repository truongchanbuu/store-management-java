package com.finalproject.storemanagementproject.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "payments")
@Data
public class Payment {
	@Id
	private String paymentId;
	private String oid;
	private String uid;
	private String paymentMethod;
    private double amount;
    private LocalDateTime paymentTime;
    private Status status;
}
