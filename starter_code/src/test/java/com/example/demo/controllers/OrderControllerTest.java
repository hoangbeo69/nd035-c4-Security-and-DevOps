package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.constants.MockConstants;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

  @Mock private UserRepository userRepository;
  @Mock private OrderRepository orderRepository;
  @InjectMocks private OrderController orderController;

  private UserOrder userOrder;

  @Before
  public void before() {
    User user = MockConstants.getUserWithCart();
    this.userOrder = UserOrder.createFromCart(user.getCart());
    when(userRepository.findByUsername("zane")).thenReturn(user);
  }

  @Test
  public void testSubmit() {
    ResponseEntity<UserOrder> response = orderController.submit("zane");

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    ArgumentCaptor<UserOrder> userOrderCaptor = ArgumentCaptor.forClass(UserOrder.class);
    verify(orderRepository, times(1)).save(userOrderCaptor.capture());

    UserOrder order = userOrderCaptor.getValue();
    assertEquals("testName", order.getItems().get(0).getName());
  }

  @Test
  public void testSubmit_EmptyUser() {
    when(userRepository.findByUsername("zane")).thenReturn(null);
    ResponseEntity<UserOrder> response = orderController.submit("zane");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void testGetOrdersForUser() throws Exception {
    when(orderRepository.findByUser(any(User.class))).thenReturn(Arrays.asList(userOrder));
    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("zane");
    assertNotNull(response);
    assertNotNull(response.getBody());

    List<Item> items = response.getBody().get(0).getItems();
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1L, (long) items.get(0).getId());
  }

  @Test
  public void testGetOrdersForUser_EmptyUser() throws Exception {
    when(userRepository.findByUsername("zane")).thenReturn(null);
    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("zane");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }


} 
