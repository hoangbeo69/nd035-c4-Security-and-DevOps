package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

  @Autowired private UserRepository userRepository;

  @Autowired private CartRepository cartRepository;

  @Autowired private ItemRepository itemRepository;

  @PostMapping("/addToCart")
  public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
    String username = request.getUsername();
    long itemId = request.getItemId();
    log.info("Try to add item id: {} to user: {}", itemId, username);
    User user = userRepository.findByUsername(username);
    if (user == null) {
      log.error("User {}, does not exist!", username);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Optional<Item> item = itemRepository.findById(itemId);
    if (Boolean.FALSE.equals(item.isPresent())) {
      log.error("Item id:{} not found!", itemId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Cart cart = user.getCart();
    IntStream.range(0, request.getQuantity()).forEach(i -> cart.addItem(item.get()));
    try {
      cartRepository.save(cart);
    } catch (Exception e) {
      log.error("Error occurred while save cart.", e);
      throw e;
    }

    return ResponseEntity.ok(cart);
  }

  @PostMapping("/removeFromCart")
  public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
    String username = request.getUsername();
    long itemId = request.getItemId();
    log.info("Try to remove item id: {} from user: {}", itemId, username);
    User user = userRepository.findByUsername(username);
    if (user == null) {
      log.error("User {}, does not exist!", username);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Optional<Item> item = itemRepository.findById(itemId);
    if (Boolean.FALSE.equals(item.isPresent())) {
      log.error("Item id:{} not found!", itemId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Cart cart = user.getCart();
    IntStream.range(0, request.getQuantity()).forEach(i -> cart.removeItem(item.get()));
    cartRepository.save(cart);
    return ResponseEntity.ok(cart);
  }
}
