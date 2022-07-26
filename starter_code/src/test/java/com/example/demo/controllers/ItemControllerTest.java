package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.demo.constants.MockConstants;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

  @Mock ItemRepository itemRepository;
  @InjectMocks ItemController itemController;

  @Before
  public void before() {
    when(itemRepository.findAll()).thenReturn(new ArrayList<>());
    when(itemRepository.findById(1L)).thenReturn(Optional.of(MockConstants.getItem()));
    when(itemRepository.findByName("zane")).thenReturn(
        new ArrayList<>(Arrays.asList(MockConstants.getItem())));
  }

  @Test
  public void testGetItems() {
    ResponseEntity<List<Item>> response = itemController.getItems();
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(0, response.getBody().size());
  }

  @Test
  public void testGetItemById() {
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNotNull(response);
    Item item = response.getBody();
    assertNotNull(item);
    assertEquals(200, response.getStatusCodeValue());

    assertEquals(1L, (long) item.getId());
    assertEquals("testName", item.getName());
    assertEquals("testDescription", item.getDescription());
    assertEquals(BigDecimal.valueOf(100), item.getPrice());

  }

  @Test
  public void testGetItemsByName() {
    ResponseEntity<List<Item>> response = itemController.getItemsByName("zane");

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals(1, response.getBody().size());
    assertEquals(1L, (long) response.getBody().get(0).getId());
  }

  @Test
  public void testGetItemsByName_returnNull() {
    when(itemRepository.findByName("zane")).thenReturn(null);

    ResponseEntity<List<Item>> response = itemController.getItemsByName("zane");

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }


} 
