package com.finalproject.storemanagementproject.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.finalproject.storemanagementproject.models.Payment;
import com.finalproject.storemanagementproject.models.Status;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
	Optional<Payment> findByOid(String oid);
    List<Payment> findByStatus(Status status);
    List<Payment> findByPaymentTimeBetween(Instant startDateTime, Instant endDateTime);
    List<Payment> findByStatusAndPaymentTimeBetween(Status status, Instant startDateTime, Instant endDateTime);
}
