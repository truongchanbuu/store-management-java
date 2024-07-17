package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.Customer;
import com.finalproject.storemanagementproject.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findById(String id) {
        return customerRepository.findById(id).orElse(null);
    }

    public List<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    public List<Customer> findByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public boolean createCustomer(Customer customer) {
        try {
            customerRepository.save(customer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updateCustomer(Customer customer) {
        try {
            customerRepository.save(customer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteCustomer(String id) {
        try {
            customerRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	public long getTotalCustomer() {
		return customerRepository.count();
	}
}
