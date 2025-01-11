package com.example.OrderService.service;

import com.example.OrderService.client.ProductDto;
import com.example.OrderService.client.ProductServiceClient;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.OrderItemDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.entity.OrderItem;
import com.example.OrderService.repository.OrderItemRepository;
import com.example.OrderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Transactional
    public OrderItemDto addOrderItem(OrderDto orderDto, OrderItemDto orderItemDto) {
        log.info("Adding order item to order with id: {}", orderDto.getId());

        // Order'ı repository üzerinden çek
        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderDto.getId()));

        // OrderItem'ı oluştur veya güncelle
        OrderItem orderItem = findOrCreateOrderItem(order, orderItemDto);
        updateOrderTotalAmount(order, orderItem.getTotalAmount());

        orderItem = orderItemRepository.save(orderItem);

        // DTO'ya dönüştür ve döndür
        OrderItemDto resultDto = new OrderItemDto();
        BeanUtils.copyProperties(orderItem, resultDto);
        return resultDto;
    }

    @Transactional
    public OrderItemDto updateOrderItem(OrderDto orderDto, Integer orderItemId, OrderItemDto orderItemDto) {
        log.info("Updating order item with id: {}", orderItemId);

        // Order'ı repository üzerinden çek
        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderDto.getId()));

        // OrderItem'ı repository üzerinden çek
        OrderItem orderItem = findOrderItemById(orderItemId);
        validateOrderItemBelongsToOrder(order, orderItem);

        double oldTotalAmount = Optional.ofNullable(orderItem.getTotalAmount()).orElse(0.0);
        updateOrderItemDetails(orderItem, orderItemDto);

        updateOrderTotalAmount(order, orderItem.getTotalAmount() - oldTotalAmount);
        orderItem = orderItemRepository.save(orderItem);

        // DTO'ya dönüştür ve döndür
        OrderItemDto resultDto = new OrderItemDto();
        BeanUtils.copyProperties(orderItem, resultDto);
        return resultDto;
    }

    @Transactional
    public void deleteOrderItem(OrderDto orderDto, Integer orderItemId) {
        log.info("Deleting order item with id: {}", orderItemId);

        // Order'ı repository üzerinden çek
        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderDto.getId()));

        // OrderItem'ı repository üzerinden çek
        OrderItem orderItem = findOrderItemById(orderItemId);
        validateOrderItemBelongsToOrder(order, orderItem);

        updateOrderTotalAmount(order, -Optional.ofNullable(orderItem.getTotalAmount()).orElse(0.0));
        order.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
    }

    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItemsByOrderId(OrderDto orderDto) {
        log.info("Getting order items for order with id: {}", orderDto.getId());

        // Order'ı repository üzerinden çek
        Order order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderDto.getId()));

        // OrderItem'ları DTO'ya dönüştür ve döndür
        return order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemDto dto = new OrderItemDto();
                    BeanUtils.copyProperties(orderItem, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private OrderItem findOrderItemById(Integer orderItemId) {
        log.info("Finding order item by id: {}", orderItemId);
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> {
                    log.error("Order item not found with id {}", orderItemId);
                    return new IllegalArgumentException("Order item not found with id " + orderItemId);
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
        OrderItem orderItem = new OrderItem();
        BeanUtils.copyProperties(orderItemDto, orderItem);
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
}