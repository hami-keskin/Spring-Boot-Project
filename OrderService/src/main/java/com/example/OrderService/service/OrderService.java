package com.example.OrderService.service;

import com.example.OrderService.annotation.RequestLogger;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.exception.RecordNotFoundException;
import com.example.OrderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequestLogger
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Cacheable("order")
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
                    throw new RecordNotFoundException("Order not found with id " + id);
                });
    }

    @CachePut(value = "order", key = "#result.id")
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Creating order: {}", orderDto);

        // Order entity'sini oluştur ve kaydet
        Order order = createOrderFromDto(orderDto);
        order = orderRepository.save(order);

        // DTO'ya dönüştür ve döndür
        OrderDto resultDto = new OrderDto();
        BeanUtils.copyProperties(order, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    @CachePut(value = "order", key = "#orderDto.id")
    public OrderDto updateOrder(Integer id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);

        // Order'ı repository üzerinden çek
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Order not found with id: " + id));

        // DTO'dan Entity'ye kopyalama
        BeanUtils.copyProperties(orderDto, order);
        order.setId(id); // ID'yi güncelle
        order = orderRepository.save(order);

        // DTO'ya dönüştür ve döndür
        OrderDto resultDto = new OrderDto();
        BeanUtils.copyProperties(order, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    @CacheEvict(value = "order", key = "#id")
    public void deleteOrder(Integer id) {
        log.info("Deleting order with id: {}", id);

        // Order'ı repository üzerinden çek ve sil
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Order not found with id: " + id));
        orderRepository.delete(order);
    }

    private Order createOrderFromDto(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        order.setTotalAmount(0.0);
        BeanUtils.copyProperties(orderDto, order); // DTO'dan Entity'ye kopyalama
        return order;
    }
}