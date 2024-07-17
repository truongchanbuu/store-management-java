package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.Order;
import com.finalproject.storemanagementproject.models.OrderProduct;
import com.finalproject.storemanagementproject.models.Status;
import com.finalproject.storemanagementproject.models.User;
import com.finalproject.storemanagementproject.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;

	public Order createOrder(User user) {
        Order createdOrder = new Order();

        createdOrder.setUser(user);

        createdOrder.setOrderStatus(Status.PENDING);
        createdOrder.setCreatedAt(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))));

        try {
            return orderRepository.save(createdOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public boolean removeOrder(Order order) {
		Order existingOrder = getOrderById(order.getOid());

		if (existingOrder == null) {
			return false;
		}

		try {
			orderRepository.delete(existingOrder);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Transactional
	public boolean updateOrder(Order order) {
	    try {
	        Order existingOrder = orderRepository.findById(order.getOid())
	                .orElseThrow(() -> new NotFoundException());

	        existingOrder.setCustomer(order.getCustomer());
	        existingOrder.setOrderProducts(order.getOrderProducts());
	        existingOrder.setUpdatedAt(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))));
	        
	        DecimalFormat df = new DecimalFormat("#.##");
	        existingOrder.setTotalPrice(Double.valueOf(df.format(calculateTotalPrice(order.getOrderProducts()))));
	        
	        Order updatedOrder = orderRepository.save(existingOrder);

	        return updatedOrder != null;
	    } catch (NotFoundException ex) {
	        // Log the exception or handle it appropriately
	        ex.printStackTrace();
	        return false;
	    }
	}

	public Order getOrderById(String oid) {
		return orderRepository.findById(oid).orElse(null);
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	private double calculateTotalPrice(List<OrderProduct> orderProducts) {
	    return orderProducts == null ? 0 :
	            orderProducts.stream()
	                    .mapToDouble(orderProduct -> orderProduct.getRetailPrice() * orderProduct.getQuantity())
	                    .filter(price -> !Double.isNaN(price) && !Double.isInfinite(price))
	                    .sum();
	}


	public Order updateOrderStatus(String orderId, Status newStatus) {
		Order existingOrder = orderRepository.findById(orderId).orElse(null);

		if (existingOrder != null) {
			existingOrder.setUpdatedAt(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))));
			existingOrder.setOrderStatus(newStatus);
			return orderRepository.save(existingOrder);
		}

		return null;
	}

	public List<Order> getOrdersByStatus(Status status) {
		if (status != null) {
			return orderRepository.findByOrderStatus(status);
		} else {
			return orderRepository.findAll();
		}
	}
	
    public List<Order> getOrdersByTimeAndStatus(Instant startDate, Instant endDate, Status status) {
        if (status != null) {
            return orderRepository.findOrdersByCreatedAtBetweenAndOrderStatus(startDate, endDate, status);
        } else {
            return orderRepository.findByCreatedAtBetween(startDate, endDate);
        }
    }

    public List<Order> findByCustomerId(String customerId) {
        return orderRepository.findByCustomerCustId(customerId);
    }

	public long getTotalOrder() {
		return orderRepository.count();
	}
}
