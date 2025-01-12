package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Cacheable("order")
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Integer id) {
        log.info("Getting order by id: {}", id);
        return orderRepository.findById(id)
                .map(order -> {
                    OrderDto orderDto = new OrderDto();
                    BeanUtils.copyProperties(order, orderDto); // Entity'den DTO'ya kopyalama
                    return orderDto;
                })
                .or(() -> {
                    log.error("Order not found with id {}", id);
                    throw new IllegalArgumentException("Order not found with id " + id);
                });
    }

    @CachePut(value = "order", key = "#result.id")
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Creating order: {}", orderDto);

        // Order entity'sini oluştur ve varsayılan değerleri ata
        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order); // DTO'dan Entity'ye kopyalama
        order.setOrderDate(LocalDateTime.now()); // Sipariş tarihi şu anki zaman olarak ayarlanır
        order.setStatus(1); // Varsayılan durum (örneğin, "Yeni Sipariş")
        order.setTotalAmount(0.0); // Başlangıçta toplam tutar 0

        // Order'ı kaydet
        order = orderRepository.save(order);

        // DTO'ya dönüştür ve döndür
        OrderDto resultDto = new OrderDto();
        BeanUtils.copyProperties(order, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    @CachePut(value = "order", key = "#orderDto.id")
    @Transactional
    public OrderDto updateOrder(Integer id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);

        // Order'ı repository üzerinden çek
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        // DTO'dan Entity'ye kopyalama
        BeanUtils.copyProperties(orderDto, order);
        order.setId(id); // ID'yi güncelle

        // Order'ı kaydet
        order = orderRepository.save(order);

        // DTO'ya dönüştür ve döndür
        OrderDto resultDto = new OrderDto();
        BeanUtils.copyProperties(order, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    @CacheEvict(value = "order", key = "#id")
    @Transactional
    public void deleteOrder(Integer id) {
        log.info("Deleting order with id: {}", id);

        // Order'ı repository üzerinden çek ve sil
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        orderRepository.delete(order);
    }
}