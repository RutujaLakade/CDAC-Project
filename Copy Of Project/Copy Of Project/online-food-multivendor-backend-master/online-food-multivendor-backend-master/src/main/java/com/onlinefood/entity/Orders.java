package com.onlinefood.entity;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Orders {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String orderId;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
	
	private String paymentId;
	@ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

	private int quantity;
	
	private String receipt;
	
	private String orderTime;
	private LocalDate orderDate;
	
	private String status;  // Processing, Delivered, On the Way // this will be for customer
	
	private int amount;
	// delivery properties
	
	// updated by seller
	@ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private User deliveryPerson;
	
	//updated by delivery person
	private String deliveryTime;  // Evening, Morning, Afternoon, Night
	
	private LocalDate deliveryDate; 
	
	private String deliveryStatus;  // Delivered, Pending // this will be for actual delivery status

	
	
}
