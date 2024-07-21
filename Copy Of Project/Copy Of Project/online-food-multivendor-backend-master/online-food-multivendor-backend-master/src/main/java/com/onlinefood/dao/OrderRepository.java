package com.onlinefood.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onlinefood.entity.Orders;
import com.onlinefood.entity.User;


public interface OrderRepository extends JpaRepository<Orders, Long> {
	
Optional<Orders> findByOrderId(String orderId);
	
	List<Orders> findAllByUser(User user);

}