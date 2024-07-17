package com.finalproject.storemanagementproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@Getter
@Setter
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String email;
    private String username;
    private String password;
    private Status status;
    private Role role;
    private String avatar;
}
