package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {


  @Autowired private UserRepository userRepository;

  @Autowired private OrderRepository orderRepository;


  @PostMapping("/submit/{username}")
  public ResponseEntity<UserOrder> submit(@PathVariable String username) {
    User user = userRepository.findByUsername(username);
    log.info("Try to find user by name: {}", username);
    if (user == null) {
      log.error("User {}, does not exist!", username);
      return ResponseEntity.notFound().build();
    }
    UserOrder order = UserOrder.createFromCart(user.getCart());
    orderRepository.save(order);
    log.info("OrderId: {} has been created by user: {}", order.getId(), username);
    return ResponseEntity.ok(order);
  }

  @GetMapping("/history/{username}")
  public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
    User user = userRepository.findByUsername(username);
    log.info("Try to get order for user: {}", username);
    if (user == null) {
      log.error("User {}, does not exist!", username);
      return ResponseEntity.notFound().build();
    }
    log.info("Success: The order of user: {} found!", username);
    return ResponseEntity.ok(orderRepository.findByUser(user));
  }
}
