package com.finalproject.storemanagementproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
@Data
@Getter
@Setter
@AllArgsConstructor
public class Customer {
    @Id
    private String custId;
    private String name;
    private String phone;
    private String email;
    private Double point;
}
