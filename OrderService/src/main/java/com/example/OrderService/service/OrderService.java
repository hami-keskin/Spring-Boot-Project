package com.example.OrderService.service;

import com.example.OrderService.annotation.RequestLogger;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.exception.RecordNotFoundException;
import com.example.OrderService.mapper.OrderMapper;
import com.example.OrderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@RequestLogger
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Cacheable("order")
    public   Optional<OrderDto> getOrderById(Integer id) {
        log.info("Getting order by id: {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .or(() -> {
                    log.error("Order not found with id {}", id);
                    throw new RecordNotFoundException("Order not found with id " + id);
                });
    }

    @CachePut(value = "order", key = "#result.id")
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Creating order: {}", orderDto);
        Order order = orderMapper.toEntity(orderDto);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(1);
        order.setTotalAmount(0.0);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @CachePut(value = "order", key = "#orderDto.id")
    public OrderDto updateOrder(Integer id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);
        Order order = orderMapper.toEntity(orderDto);
        order.setId(id);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @CacheEvict(value = "order", key = "#id")
    public void deleteOrder(Integer id) {
        log.info("Deleting order with id: {}", id);
        orderRepository.deleteById(id);
    }
}
