package com.finalproject.storemanagementproject.repositories;

import com.finalproject.storemanagementproject.models.Order;
import com.finalproject.storemanagementproject.models.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByOrderStatus(Status status);

    @Query("{ 'createdAt' : { $gte: ?0, $lte: ?1 }, 'orderStatus': ?2 }")
    List<Order> findOrdersByCreatedAtBetweenAndOrderStatus(Instant startDate, Instant endDate, Status status);

    List<Order> findByCreatedAtBetween(Instant startInstant, Instant endInstant);

    List<Order> findByCustomerCustId(String custId);
}
