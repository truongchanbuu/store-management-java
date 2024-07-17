package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.OrderProduct;
import com.finalproject.storemanagementproject.repositories.OrderProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderProductService {
    @Autowired
    private OrderProductRepository orderProductRepository;

    public boolean addOrderProduct(OrderProduct orderProduct) {
        OrderProduct addedProduct = null;
        try {
            addedProduct = orderProductRepository.insert(orderProduct);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return addedProduct != null;
    }
    
    @Transactional
    public OrderProduct createOrderProducts(OrderProduct orderProduct) {
        try {
        	return orderProductRepository.save(orderProduct);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
   }

	public List<OrderProduct> getAllOrderProductsByOid(String oid) {
        return orderProductRepository.findAllByOid(oid);
	}

	public OrderProduct findByOidAndPid(String oid, String pid) {
        return orderProductRepository.findByOidAndPid(oid, pid);
	}

	public OrderProduct updateOrderProduct(OrderProduct existingOrderProduct) {
		try {
	        return orderProductRepository.save(existingOrderProduct);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean deleteOrderProduct(String oid, String pid) {
        OrderProduct orderProduct = orderProductRepository.findByOidAndPid(oid, pid);

        if (orderProduct != null) {
        	try {
                orderProductRepository.delete(orderProduct);
                return true;
        	} catch (Exception e) {
        		e.printStackTrace();
        		return false;
        	}
        } else {
            return false;
        }
    }

    public List<OrderProduct> getOrderProductsByPid(String pid) {
        return orderProductRepository.findAllByPid(pid);
    }
}
