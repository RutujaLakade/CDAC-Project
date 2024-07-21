package com.onlinefood.controller;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.onlinefood.dao.OrderRepository;
import com.onlinefood.dto.CommonApiResponse;
import com.onlinefood.entity.Orders;
import com.onlinefood.entity.Packate;
import com.onlinefood.entity.PaymentUpdatePackate;
import com.onlinefood.entity.User;
import com.onlinefood.service.FoodServiceImpl;
import com.onlinefood.service.UserServiceImpl;

@RestController
@CrossOrigin
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private FoodServiceImpl foodService;

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody Packate order) {
        try {
            int amt = order.getAmount()*100;
            System.err.println(order.getName());
            RazorpayClient client = new RazorpayClient("rzp_test_E4MkK7Plh7O8qI", "R8oU0KDghQrGhRy7V6xxiXFF");

            JSONObject ob = new JSONObject();
            ob.put("amount", amt);
            ob.put("currency", "INR");
            ob.put("receipt", "txn_255425");

            Order orderToRazorpay = client.orders.create(ob);
            
            System.out.println((int) orderToRazorpay.get("amount"));
            Orders orderEntity = new Orders();
            
            orderEntity.setAmount((int) orderToRazorpay.get("amount"));
            orderEntity.setOrderId(orderToRazorpay.get("id"));
            orderEntity.setPaymentId(null);
            orderEntity.setStatus("created");
            orderEntity.setReceipt(orderToRazorpay.get("receipt"));
            orderEntity.setOrderDate(LocalDate.now());
            LocalDate date = LocalDate.now();
            orderEntity.setDeliveryDate(date.plusDays(2));
            orderRepo.save(orderEntity);
            return ResponseEntity.ok(orderToRazorpay.toString());
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/order/update")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody PaymentUpdatePackate packate) {
        Orders order = orderRepo.findByOrderId(packate.getOrder_id()).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order with ID " + packate.getOrder_id() + " not found");
        }

        order.setPaymentId(packate.getPayment_id());
        order.setStatus(packate.getPayment_status().toString());

        Orders updatedOrder = orderRepo.save(order);
        System.out.println(packate.toString());

        return ResponseEntity.ok("Order Updated Successfully!!!!" + updatedOrder.getId());
    }

    // get all orders
    @GetMapping("")
    public ResponseEntity<?> getAllOrders() {
        List<Orders> orders = orderRepo.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order List is Empty!!!");
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAllUserOrders(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        List<Orders> orders = orderRepo.findAllByUser(user);
        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order List is Empty!!!");
        }
        return ResponseEntity.ok(orders);
    }
}
