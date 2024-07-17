package com.finalproject.storemanagementproject.repositories;

import com.finalproject.storemanagementproject.models.OrderProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends MongoRepository<OrderProduct, Integer> {
	  List<OrderProduct> findAllByOid(String oid);
    OrderProduct findByOidAndPid(String oid, String pid);
    List<OrderProduct> findAllByPid(String pid);
}
