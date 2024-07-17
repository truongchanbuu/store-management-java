package com.finalproject.storemanagementproject.controllers;

import com.finalproject.storemanagementproject.models.APIResponse;
import com.finalproject.storemanagementproject.models.Order;
import com.finalproject.storemanagementproject.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("")
    public ResponseEntity<APIResponse<Order>> getOrders(@RequestParam(required = false) String customerId) {
        List<Order> orders = null;
        if (customerId != null && !customerId.isEmpty())
            orders = orderService.findByCustomerId(customerId);
        else orders = orderService.getAllOrders();

        if (orders == null) {
            return ResponseEntity.ok(
                    new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", Collections.emptyList()));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.OK.value(), "Success", orders));
    }
    
    @GetMapping("/total")
    public ResponseEntity<APIResponse<Long>> getTotalOrder() {
    	long total = orderService.getTotalOrder();
    	return ResponseEntity.ok(new APIResponse<Long>(HttpStatus.OK.value(), "Success", Collections.singletonList(total)));
    }
}
