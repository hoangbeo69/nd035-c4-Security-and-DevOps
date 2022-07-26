package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @GetMapping("/id/{id}")
  public ResponseEntity<User> findById(@PathVariable Long id) {
    log.info("Try to find user by id: {}", id);
    return ResponseEntity.of(userRepository.findById(id));
  }

  @GetMapping("/{username}")
  public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("Try to find user by name: {}", username);
    User user = userRepository.findByUsername(username);
    if (user == null) {
			log.error("User {}, does not exist", username);
      return ResponseEntity.notFound().build();
    }
		log.info("Success: User: {} found!", username);
    return ResponseEntity.ok(user);
  }

  @PostMapping("/create")
  public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
    User user = new User();
    user.setUsername(createUserRequest.getUsername());
		log.info("try to create user: {}", user.getUsername());
    Cart cart = new Cart();
    cartRepository.save(cart);
    user.setCart(cart);
    // if length not right or confirm password not match, return 400
    if (createUserRequest.getPassword().length() < 7 ||
        !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.error(
          "User was not created, because password is bigger than 7 characters or password does not match");
      return ResponseEntity.badRequest().build();
    }
    // Otherwise save this encode password using bCryptPassword
    user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

    userRepository.save(user);
		log.info("Success: User {}, been created!", user.getUsername());
    return ResponseEntity.ok(user);
  }

}
