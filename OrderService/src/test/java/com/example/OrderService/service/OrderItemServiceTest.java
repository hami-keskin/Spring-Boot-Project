package com.example.OrderService.service;

import com.example.OrderService.client.ProductDto;
import com.example.OrderService.client.ProductServiceClient;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.OrderItemDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;
import com.example.OrderService.exception.RecordNotFoundException;
import com.example.OrderService.repository.OrderItemRepository;
import com.example.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class OrderItemServiceTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private ProductServiceClient productServiceClient;

    private OrderDto orderDto;
    private OrderItemDto orderItemDto;
    private Order order;
    private ProductDto productDto;

    @BeforeEach
    public void setUp() {
        // Test verilerini hazırla
        orderDto = new OrderDto();
        orderDto.setId(1);

        orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(101);
        orderItemDto.setQuantity(2);

        order = new Order();
        order.setId(1);
        order.setTotalAmount(0.0);
        order.setOrderItems(Collections.emptyList());

        productDto = new ProductDto();
        productDto.setId(101);
        productDto.setPrice(50.0);

        // Order'ı repository'e kaydet
        orderRepository.save(order);

        // ProductServiceClient mock'u
        when(productServiceClient.getProductById(any(Integer.class))).thenReturn(productDto);
    }

    @Test
    public void testAddOrderItem() {
        // OrderItem ekle
        OrderItemDto result = orderItemService.addOrderItem(orderDto, orderItemDto);

        // Sonuçları doğrula
        assertNotNull(result);
        assertEquals(orderItemDto.getProductId(), result.getProductId());
        assertEquals(orderItemDto.getQuantity(), result.getQuantity());
        assertEquals(100.0, result.getTotalAmount()); // 2 * 50.0 = 100.0

        // Order'ın totalAmount'unun güncellendiğini doğrula
        Order updatedOrder = orderRepository.findById(orderDto.getId()).orElseThrow();
        assertEquals(100.0, updatedOrder.getTotalAmount());
    }

    @Test
    public void testUpdateOrderItem() {
        // Önce bir OrderItem ekle
        OrderItemDto addedItem = orderItemService.addOrderItem(orderDto, orderItemDto);

        // OrderItem'ı güncelle
        OrderItemDto updatedItemDto = new OrderItemDto();
        updatedItemDto.setProductId(101);
        updatedItemDto.setQuantity(3);

        OrderItemDto result = orderItemService.updateOrderItem(orderDto, addedItem.getId(), updatedItemDto);

        // Sonuçları doğrula
        assertNotNull(result);
        assertEquals(updatedItemDto.getQuantity(), result.getQuantity());
        assertEquals(150.0, result.getTotalAmount()); // 3 * 50.0 = 150.0

        // Order'ın totalAmount'unun güncellendiğini doğrula
        Order updatedOrder = orderRepository.findById(orderDto.getId()).orElseThrow();
        assertEquals(150.0, updatedOrder.getTotalAmount());
    }

    @Test
    public void testDeleteOrderItem() {
        // Önce bir OrderItem ekle
        OrderItemDto addedItem = orderItemService.addOrderItem(orderDto, orderItemDto);

        // OrderItem'ı sil
        orderItemService.deleteOrderItem(orderDto, addedItem.getId());

        // OrderItem'ın silindiğini doğrula
        assertFalse(orderItemRepository.findById(addedItem.getId()).isPresent());

        // Order'ın totalAmount'unun güncellendiğini doğrula
        Order updatedOrder = orderRepository.findById(orderDto.getId()).orElseThrow();
        assertEquals(0.0, updatedOrder.getTotalAmount());
    }

    @Test
    public void testGetOrderItemsByOrderId() {
        // Önce bir OrderItem ekle
        OrderItemDto addedItem = orderItemService.addOrderItem(orderDto, orderItemDto);

        // OrderItem'ları getir
        List<OrderItemDto> result = orderItemService.getOrderItemsByOrderId(orderDto);

        // Sonuçları doğrula
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(addedItem.getId(), result.get(0).getId());
    }

    @Test
    public void testAddOrderItem_OrderNotFound() {
        // Var olmayan bir Order ID'si ile OrderItem eklemeye çalış
        OrderDto nonExistentOrderDto = new OrderDto();
        nonExistentOrderDto.setId(999);

        assertThrows(RecordNotFoundException.class, () -> {
            orderItemService.addOrderItem(nonExistentOrderDto, orderItemDto);
        });
    }

    @Test
    public void testUpdateOrderItem_OrderItemNotFound() {
        // Var olmayan bir OrderItem ID'si ile güncelleme yapmaya çalış
        assertThrows(RecordNotFoundException.class, () -> {
            orderItemService.updateOrderItem(orderDto, 999, orderItemDto);
        });
    }

    @Test
    public void testDeleteOrderItem_OrderItemNotFound() {
        // Var olmayan bir OrderItem ID'si ile silme işlemi yapmaya çalış
        assertThrows(RecordNotFoundException.class, () -> {
            orderItemService.deleteOrderItem(orderDto, 999);
        });
    }
}