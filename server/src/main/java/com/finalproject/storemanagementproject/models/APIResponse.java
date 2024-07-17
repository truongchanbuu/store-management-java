package com.finalproject.storemanagementproject.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponse<T> {
    private Integer code;
    private String message;
    private List<T> data;
}
