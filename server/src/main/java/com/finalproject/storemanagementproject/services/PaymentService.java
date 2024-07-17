package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.Order;
import com.finalproject.storemanagementproject.models.Payment;
import com.finalproject.storemanagementproject.models.Status;
import com.finalproject.storemanagementproject.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public boolean createPayment(Order order, String paymentMethod) {
        Payment payment = new Payment();

        payment.setStatus(Status.PENDING);
        payment.setOid(order.getOid());
        payment.setPaymentTime(LocalDateTime.now());
        payment.setAmount(order.getTotalPrice());
        payment.setUid(order.getUser().getId());
        payment.setPaymentMethod(paymentMethod);

        try {
            paymentRepository.save(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Payment getPaymentByOid(String oid) {
        return paymentRepository.findByOid(oid).orElse(null);
    }

    public boolean updatePaymentMethod(String oid, String paymentMethod) {
        Payment existingPayment = getPaymentByOid(oid);

        if (existingPayment == null) {
            return false;
        }

        try {
            existingPayment.setPaymentMethod(paymentMethod);
            paymentRepository.save(existingPayment);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePaymentStatus(String oid, Status paymentStatus) {
        Payment existingPayment = getPaymentByOid(oid);

        if (existingPayment == null) {
            return false;
        }

        try {
            existingPayment.setStatus(paymentStatus);
            paymentRepository.save(existingPayment);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getPaymentByBetweenDate(Instant startDate, Instant endDate) {
        return paymentRepository.findByPaymentTimeBetween(startDate, endDate);
    }

    public List<Payment> getPaymentsInCurrentMonth(LocalDate currentDate) {
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);
        LocalDate endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        Instant startOfMonthInstant = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfMonthInstant = endOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        return paymentRepository.findByPaymentTimeBetween(startOfMonthInstant, endOfMonthInstant);
    }

    public List<Payment> getPaymentByStatusAtDate(Status completed, Instant startDate, Instant endDate) {
        return paymentRepository.findByStatusAndPaymentTimeBetween(completed, startDate, endDate);
    }
}
