package com.finalproject.storemanagementproject.models;

import java.util.List;

import lombok.Data;

@Data
public class AnalyticsReport {
	private double totalAmountReceived;
    private int numberOfOrders;
    private int numberOfProducts;
    private List<Order> orders;
}
