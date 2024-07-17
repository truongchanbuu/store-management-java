package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.Order;
import com.finalproject.storemanagementproject.models.OrderProduct;
import com.finalproject.storemanagementproject.models.Product;
import com.finalproject.storemanagementproject.repositories.OrderRepository;
import com.finalproject.storemanagementproject.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> findByBarCode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    public List<Product> findProductByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product findByPid(String pid) {
        return productRepository.findById(pid).orElse(null);
    }

	public long getTotalProducts() {
		return productRepository.count();
	}
}
