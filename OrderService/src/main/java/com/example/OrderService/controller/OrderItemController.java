package com.example.OrderService.controller;

import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.dto.OrderItemDto;
import com.example.OrderService.service.OrderItemService;
import com.example.OrderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;

    static final String ORDER_NOT_FOUND_MESSAGE = "Order not found"; // Sabit tanımlandı

    private OrderDto getOrderOrThrow(Integer orderId) {
        return orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException(ORDER_NOT_FOUND_MESSAGE));
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> addOrderItem(@PathVariable Integer orderId, @RequestBody OrderItemDto orderItemDto) {
        OrderItemDto createdOrderItem = orderItemService.addOrderItem(getOrderOrThrow(orderId), orderItemDto);
        return ResponseEntity.ok(createdOrderItem);
    }

    @PutMapping("/{orderItemId}")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Integer orderId, @PathVariable Integer orderItemId, @RequestBody OrderItemDto orderItemDto) {
        OrderItemDto updatedOrderItem = orderItemService.updateOrderItem(getOrderOrThrow(orderId), orderItemId, orderItemDto);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Integer orderId, @PathVariable Integer orderItemId) {
        orderItemService.deleteOrderItem(getOrderOrThrow(orderId), orderItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByOrderId(@PathVariable Integer orderId) {
        List<OrderItemDto> orderItems = orderItemService.getOrderItemsByOrderId(getOrderOrThrow(orderId));
        return ResponseEntity.ok(orderItems);
    }
}
