package com.example.demo.controllers;

import static com.example.demo.constants.MockConstants.getItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.demo.constants.MockConstants;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

  @Mock private UserRepository userRepository;
  @Mock private CartRepository cartRepository;
  @Mock private ItemRepository itemRepository;

  @InjectMocks private CartController cartController;

  private ModifyCartRequest mcr;

  @Before
  public void before() {
    when(userRepository.findByUsername("zane")).thenReturn(MockConstants.getUser());
    when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem()));

    mcr = MockConstants.getRequest();
  }

  @Test
  public void testAddToCart() {
    ResponseEntity<Cart> response = cartController.addToCart(mcr);

    assertNotNull(response);
    assertNotNull(response.getBody());
    Cart cart = response.getBody();
    Item item = cart.getItems().get(0);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, response.getBody().getItems().size());
    assertEquals(1L, (long) item.getId());
    assertEquals("testName", item.getName());
    assertEquals("testDescription", item.getDescription());
    assertEquals(BigDecimal.valueOf(100), item.getPrice());
  }

  @Test(expected = RuntimeException.class)
  public void testAddToCart_failed() {
    doThrow(new RuntimeException("Cart Repository error!")).when(cartRepository)
        .save(any(Cart.class));
    cartController.addToCart(mcr);
  }

  @Test
  public void testAddToCart_NotFound() {
    mcr.setUsername("not_found");
    ResponseEntity<Cart> response = cartController.addToCart(mcr);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void testRemoveFromCart() throws Exception {
    ResponseEntity<Cart> response = cartController.removeFromCart(mcr);
    assertNotNull(response);
    assertNotNull(response.getBody());

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(0, response.getBody().getItems().size());
  }

  @Test
  public void testRemoveFromCart_NotFound() {
    mcr.setUsername("not_found");
    ResponseEntity<Cart> response = cartController.removeFromCart(mcr);
    assertEquals(404, response.getStatusCodeValue());
  }
} 
