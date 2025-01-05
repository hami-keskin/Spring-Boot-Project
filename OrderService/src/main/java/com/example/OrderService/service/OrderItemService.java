package com.example.OrderService.service;

import com.example.OrderService.client.ProductDto;
import com.example.OrderService.client.ProductServiceClient;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.OrderItemDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;
import com.example.OrderService.exception.RecordNotFoundException;
import com.example.OrderService.mapper.OrderItemMapper;
import com.example.OrderService.mapper.OrderMapper;
import com.example.OrderService.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;

    // Artık self-injection'a gerek yok, direkt bu sınıf üzerinden çağıracağız
    protected OrderItemService getSelf() {
        return this;
    }

    @Transactional
    public OrderItemDto addOrderItem(OrderDto orderDto, OrderItemDto orderItemDto) {
        log.info("Adding order item to order with id: {}", orderDto.getId());
        Order order = orderMapper.toEntity(orderDto);  // DTO'dan entity'ye dönüşüm
        OrderItem orderItem = findOrCreateOrderItem(order, orderItemDto);
        updateOrderTotalAmount(order, orderItem.getTotalAmount());

        orderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Transactional
    public OrderItemDto updateOrderItem(OrderDto orderDto, Integer orderItemId, OrderItemDto orderItemDto) {
        log.info("Updating order item with id: {}", orderItemId);
        Order order = orderMapper.toEntity(orderDto);  // DTO'dan entity'ye dönüşüm
        OrderItem orderItem = findOrderItemById(orderItemId);

        validateOrderItemBelongsToOrder(order, orderItem);
        getSelf().handleQuantityZero(orderDto, orderItemDto, orderItemId);  // Self-invocation

        double oldTotalAmount = Optional.ofNullable(orderItem.getTotalAmount()).orElse(0.0);
        updateOrderItemDetails(orderItem, orderItemDto);

        updateOrderTotalAmount(order, orderItem.getTotalAmount() - oldTotalAmount);
        orderItem = orderItemRepository.save(orderItem);

        return orderItemMapper.toDto(orderItem);
    }

    @Transactional
    public void deleteOrderItem(OrderDto orderDto, Integer orderItemId) {
        log.info("Deleting order item with id: {}", orderItemId);
        Order order = orderMapper.toEntity(orderDto);  // DTO'dan entity'ye dönüşüm
        OrderItem orderItem = findOrderItemById(orderItemId);

        validateOrderItemBelongsToOrder(order, orderItem);
        updateOrderTotalAmount(order, -Optional.ofNullable(orderItem.getTotalAmount()).orElse(0.0));

        order.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
    }

    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItemsByOrderId(OrderDto orderDto) {
        log.info("Getting order items for order with id: {}", orderDto.getId());
        Order order = orderMapper.toEntity(orderDto);  // DTO'dan entity'ye dönüşüm
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    OrderItem findOrderItemById(Integer orderItemId) {
        log.info("Finding order item by id: {}", orderItemId);
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> {
                    log.error("Order item not found with id {}", orderItemId);
                    return new RecordNotFoundException("Order item not found with id " + orderItemId);
                });
    }

    private OrderItem findOrCreateOrderItem(Order order, OrderItemDto orderItemDto) {
        return order.getOrderItems().stream()
                .filter(item -> item.getProductId().equals(orderItemDto.getProductId()))
                .findFirst()
                .map(existingOrderItem -> {
                    updateOrderTotalAmount(order, -Optional.ofNullable(existingOrderItem.getTotalAmount()).orElse(0.0));
                    existingOrderItem.setQuantity(existingOrderItem.getQuantity() + orderItemDto.getQuantity());
                    updateOrderItemTotalAmount(existingOrderItem);
                    return existingOrderItem;
                })
                .orElseGet(() -> createNewOrderItem(order, orderItemDto));
    }

    private OrderItem createNewOrderItem(Order order, OrderItemDto orderItemDto) {
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDto);
        updateOrderItemTotalAmount(orderItem);
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);
        return orderItem;
    }

    private void updateOrderItemTotalAmount(OrderItem orderItem) {
        ProductDto productDto = productServiceClient.getProductById(orderItem.getProductId());
        orderItem.setPrice(productDto.getPrice());
        orderItem.setTotalAmount(productDto.getPrice() * orderItem.getQuantity());
    }

    private void updateOrderTotalAmount(Order order, double amount) {
        order.setTotalAmount(Optional.ofNullable(order.getTotalAmount()).orElse(0.0) + amount);
    }

    private void updateOrderItemDetails(OrderItem orderItem, OrderItemDto orderItemDto) {
        orderItem.setQuantity(orderItemDto.getQuantity());
        updateOrderItemTotalAmount(orderItem);
    }

    private void validateOrderItemBelongsToOrder(Order order, OrderItem orderItem) {
        if (!order.getOrderItems().contains(orderItem)) {
            throw new IllegalArgumentException("Order item does not belong to the specified order.");
        }
    }

    private void handleQuantityZero(OrderDto orderDto, OrderItemDto orderItemDto, Integer orderItemId) {
        if (orderItemDto.getQuantity() <= 0) {
            getSelf().deleteOrderItem(orderDto, orderItemId);  // Self-invocation via injected dependency
        }
    }
}
