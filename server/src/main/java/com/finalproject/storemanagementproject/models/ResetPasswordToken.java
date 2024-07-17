package com.finalproject.storemanagementproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "password_reset_token")
@Data
@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordToken {
    @Id
    private String id;
    private String userId;
    private LocalDateTime expiryDate;
}
