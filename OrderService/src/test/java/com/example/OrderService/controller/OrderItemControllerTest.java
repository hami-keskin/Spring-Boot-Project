package com.example.OrderService.controller;

import com.example.OrderService.dto.OrderItemDto;
import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.service.OrderItemService;
import com.example.OrderService.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class OrderItemControllerTest {

    private OrderItemController orderItemController;
    private OrderItemService orderItemService;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderItemService = Mockito.mock(OrderItemService.class);
        orderService = Mockito.mock(OrderService.class);
        orderItemController = new OrderItemController(orderItemService, orderService);
    }

    @Test
    public void testAddOrderItem_Success() {
        OrderDto orderDto = new OrderDto();
        OrderItemDto orderItemDto = new OrderItemDto();
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.of(orderDto));
        when(orderItemService.addOrderItem(any(OrderDto.class), any(OrderItemDto.class))).thenReturn(orderItemDto);

        ResponseEntity<OrderItemDto> response = orderItemController.addOrderItem(1, orderItemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderItemDto);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(1)).addOrderItem(any(OrderDto.class), any(OrderItemDto.class));
    }

    @Test
    public void testUpdateOrderItem_Success() {
        OrderDto orderDto = new OrderDto();
        OrderItemDto orderItemDto = new OrderItemDto();
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.of(orderDto));
        when(orderItemService.updateOrderItem(any(OrderDto.class), anyInt(), any(OrderItemDto.class))).thenReturn(orderItemDto);

        ResponseEntity<OrderItemDto> response = orderItemController.updateOrderItem(1, 1, orderItemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderItemDto);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(1)).updateOrderItem(any(OrderDto.class), anyInt(), any(OrderItemDto.class));
    }

    @Test
    public void testDeleteOrderItem_Success() {
        OrderDto orderDto = new OrderDto();
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.of(orderDto));
        doNothing().when(orderItemService).deleteOrderItem(any(OrderDto.class), anyInt());

        ResponseEntity<Void> response = orderItemController.deleteOrderItem(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(1)).deleteOrderItem(any(OrderDto.class), anyInt());
    }

    @Test
    public void testGetOrderItemsByOrderId_Success() {
        OrderDto orderDto = new OrderDto();
        List<OrderItemDto> orderItems = Arrays.asList(new OrderItemDto(), new OrderItemDto());
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.of(orderDto));
        when(orderItemService.getOrderItemsByOrderId(any(OrderDto.class))).thenReturn(orderItems);

        ResponseEntity<List<OrderItemDto>> response = orderItemController.getOrderItemsByOrderId(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(orderItems);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(1)).getOrderItemsByOrderId(any(OrderDto.class));
    }

    @Test
    public void testAddOrderItem_OrderNotFound() {
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> orderItemController.addOrderItem(1, new OrderItemDto()));

        assertThat(exception.getMessage()).isEqualTo(OrderItemController.ORDER_NOT_FOUND_MESSAGE);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(0)).addOrderItem(any(OrderDto.class), any(OrderItemDto.class));
    }

    @Test
    public void testUpdateOrderItem_OrderNotFound() {
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> orderItemController.updateOrderItem(1, 1, new OrderItemDto()));

        assertThat(exception.getMessage()).isEqualTo(OrderItemController.ORDER_NOT_FOUND_MESSAGE);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(0)).updateOrderItem(any(OrderDto.class), anyInt(), any(OrderItemDto.class));
    }

    @Test
    public void testDeleteOrderItem_OrderNotFound() {
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> orderItemController.deleteOrderItem(1, 1));

        assertThat(exception.getMessage()).isEqualTo(OrderItemController.ORDER_NOT_FOUND_MESSAGE);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(0)).deleteOrderItem(any(OrderDto.class), anyInt());
    }

    @Test
    public void testGetOrderItemsByOrderId_OrderNotFound() {
        when(orderService.getOrderById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> orderItemController.getOrderItemsByOrderId(1));

        assertThat(exception.getMessage()).isEqualTo(OrderItemController.ORDER_NOT_FOUND_MESSAGE);
        verify(orderService, times(1)).getOrderById(1);
        verify(orderItemService, times(0)).getOrderItemsByOrderId(any(OrderDto.class));
    }
}
