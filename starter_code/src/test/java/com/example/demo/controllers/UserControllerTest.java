package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.constants.MockConstants;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

  @Mock private UserRepository userRepository;
  @Mock private CartRepository cartRepository;
  @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
  @InjectMocks private UserController userController;

  @Before
  public void before() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(MockConstants.getUser()));
    when(userRepository.findByUsername("zane")).thenReturn(MockConstants.getUser());
  }

  @Test
  public void testFindById() {
    ResponseEntity<User> response = userController.findById(1L);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void testFindByUserName() {
    ResponseEntity<User> response = userController.findByUserName("zane");
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void testFindByUserName_EmptyUser() {
    when(userRepository.findByUsername("zane")).thenReturn(null);
    ResponseEntity<User> response = userController.findByUserName("zane");
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void testCreateUser_invalidPassword() {
    ResponseEntity<User> response = userController.createUser(
        MockConstants.getUserRequest_invalidPwd());
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void testCreateUser() {
    ResponseEntity<User> response = userController.createUser(MockConstants.getUserRequest());
    assertEquals(200, response.getStatusCodeValue());
  }

} 
