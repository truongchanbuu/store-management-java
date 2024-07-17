package com.finalproject.storemanagementproject.controllers;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finalproject.storemanagementproject.models.APIResponse;
import com.finalproject.storemanagementproject.models.Customer;
import com.finalproject.storemanagementproject.services.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Customer>> getCustomerById(@PathVariable String id) {
        Customer customer = customerService.findById(id);

        if (customer == null) {
            return ResponseEntity.ok(
                    new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", Collections.emptyList()));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.OK.value(), "Success", Collections.singletonList(customer)));
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<Customer>> getCustomerByPhone(@RequestParam(required = false) String phone, @RequestParam(required = false) String name) {
        List<Customer> customers;

        if (name != null && !name.isEmpty())
            customers = customerService.findByName(name);
        else customers = customerService.getAllCustomers();

        if (phone != null && !phone.isEmpty())
            customers.removeIf(customer -> !customer.getPhone().equals(phone));

        if (customers == null) {
            return ResponseEntity.ok(
                    new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", Collections.emptyList()));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.OK.value(), "Success", customers));
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Customer>> createCustomer(@RequestBody Customer customer) {
        customer.setPoint(Double.valueOf(0));
        
        // 1 account 1 phone number
        List<Customer> customersByPhone = customerService.findByPhone(customer.getPhone());
        if (customersByPhone.size() != 0) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "There is an customer with this phone", null));
        }
        
        boolean isCreated = customerService.createCustomer(customer);

        if (!isCreated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Failed to add", Collections.singletonList(customer)));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.CREATED.value(), "Success", Collections.singletonList(customer)));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<APIResponse<Customer>> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        Customer customerData = customerService.findById(id);

        if (customerData == null) {
            return ResponseEntity.ok(
                    new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", Collections.emptyList()));
        }

        if (customer.getName() == null || customer.getName().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Name is required", Collections.emptyList()));

        if (customer.getPhone() == null || customer.getPhone().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Phone is required", Collections.emptyList()));

        if (customer.getEmail() == null || customer.getEmail().isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Email is required", Collections.emptyList()));

        if (customer.getPoint() == null || customer.getPoint().isNaN() || customer.getPoint() < 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid point", Collections.emptyList()));

        if (!customer.getEmail().matches("^\\w+@\\w+\\.\\w+$"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid email", Collections.emptyList()));

        customerData.setName(customer.getName());
        customerData.setPhone(customer.getPhone());
        customerData.setEmail(customer.getEmail());
        customerData.setPoint(customer.getPoint());

        boolean isUpdated = customerService.createCustomer(customerData);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Failed to update", Collections.emptyList()));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.OK.value(), "Success", Collections.singletonList(customerData)));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<APIResponse<Customer>> deleteCustomer(@PathVariable String id) {
        Customer customer = customerService.findById(id);

        if (customer == null) {
            return ResponseEntity.ok(
                    new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", Collections.emptyList()));
        }

        boolean isDeleted = customerService.deleteCustomer(id);

        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete", Collections.singletonList(customer)));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.OK.value(), "Success", Collections.singletonList(customer)));
    }
    
    @GetMapping("/total")
    public ResponseEntity<APIResponse<Long>> getTotalCustomer() {
    	long total = customerService.getTotalCustomer();
    	return ResponseEntity.ok(new APIResponse<Long>(HttpStatus.OK.value(), "Success", Collections.singletonList(total)));
    }
}
