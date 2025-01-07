package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.exception.RecordNotFoundException;
import com.example.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CacheManager cacheManager;

    private OrderDto orderDto;

    @BeforeEach
    public void setUp() {
        // Test verilerini hazırla
        orderDto = new OrderDto();
        orderDto.setId(1);
        orderDto.setTotalAmount(100.0);
    }

    @Test
    public void testGetOrderById() {
        // Order oluştur ve kaydet
        Order order = new Order();
        order.setId(1);
        order.setTotalAmount(100.0);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        orderRepository.save(order);

        // Order'ı getir
        OrderDto result = orderService.getOrderById(1).orElseThrow();

        // Sonuçları doğrula
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getTotalAmount(), result.getTotalAmount());

        // Cache'den doğru verinin alındığını doğrula
        OrderDto cachedOrder = cacheManager.getCache("order").get(1, OrderDto.class);
        assertNotNull(cachedOrder);
        assertEquals(order.getId(), cachedOrder.getId());
    }

    @Test
    public void testGetOrderById_OrderNotFound() {
        // Var olmayan bir Order ID'si ile getirme işlemi yapmaya çalış
        assertThrows(RecordNotFoundException.class, () -> {
            orderService.getOrderById(999).orElseThrow();
        });
    }

    @Test
    public void testCreateOrder() {
        // Order oluştur
        OrderDto result = orderService.createOrder(orderDto);

        // Sonuçları doğrula
        assertNotNull(result);
        assertEquals(orderDto.getTotalAmount(), result.getTotalAmount());

        // Order'ın repository'de kaydedildiğini doğrula
        Order savedOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertEquals(result.getId(), savedOrder.getId());

        // Cache'den doğru verinin alındığını doğrula
        OrderDto cachedOrder = cacheManager.getCache("order").get(result.getId(), OrderDto.class);
        assertNotNull(cachedOrder);
        assertEquals(result.getId(), cachedOrder.getId());
    }

    @Test
    public void testUpdateOrder() {
        // Order oluştur ve kaydet
        Order order = new Order();
        order.setId(1);
        order.setTotalAmount(100.0);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        orderRepository.save(order);

        // Order'ı güncelle
        OrderDto updatedOrderDto = new OrderDto();
        updatedOrderDto.setId(1);
        updatedOrderDto.setTotalAmount(200.0);

        OrderDto result = orderService.updateOrder(1, updatedOrderDto);

        // Sonuçları doğrula
        assertNotNull(result);
        assertEquals(updatedOrderDto.getTotalAmount(), result.getTotalAmount());

        // Order'ın repository'de güncellendiğini doğrula
        Order updatedOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertEquals(result.getTotalAmount(), updatedOrder.getTotalAmount());

        // Cache'den doğru verinin alındığını doğrula
        OrderDto cachedOrder = cacheManager.getCache("order").get(result.getId(), OrderDto.class);
        assertNotNull(cachedOrder);
        assertEquals(result.getId(), cachedOrder.getId());
    }

    @Test
    public void testUpdateOrder_OrderNotFound() {
        // Var olmayan bir Order ID'si ile güncelleme işlemi yapmaya çalış
        assertThrows(RecordNotFoundException.class, () -> {
            orderService.updateOrder(999, orderDto);
        });
    }

    @Test
    public void testDeleteOrder() {
        // Order oluştur ve kaydet
        Order order = new Order();
        order.setId(1);
        order.setTotalAmount(100.0);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        orderRepository.save(order);

        // Order'ı sil
        orderService.deleteOrder(1);

        // Order'ın repository'den silindiğini doğrula
        assertFalse(orderRepository.findById(1).isPresent());

        // Cache'den verinin silindiğini doğrula
        assertNull(cacheManager.getCache("order").get(1, OrderDto.class));
    }

    @Test
    public void testDeleteOrder_OrderNotFound() {
        // Var olmayan bir Order ID'si ile silme işlemi yapmaya çalış
        assertThrows(RecordNotFoundException.class, () -> {
            orderService.deleteOrder(999);
        });
    }
}