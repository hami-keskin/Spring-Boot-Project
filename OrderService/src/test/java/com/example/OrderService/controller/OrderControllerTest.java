package com.example.OrderService.controller;

import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = Mockito.mock(OrderService.class);
        orderController = new OrderController(orderService);
    }

    @Test
    public void testGetOrderById_Success() {
        OrderDto orderDto = new OrderDto();
        when(orderService.getOrderById(1)).thenReturn(Optional.of(orderDto));

        ResponseEntity<OrderDto> response = orderController.getOrderById(1);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(orderDto);
        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderService.getOrderById(1)).thenReturn(Optional.empty());

        ResponseEntity<OrderDto> response = orderController.getOrderById(1);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        verify(orderService, times(1)).getOrderById(1);
    }

    @Test
    public void testCreateOrder() {
        OrderDto orderDto = new OrderDto();
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(orderDto);

        ResponseEntity<OrderDto> response = orderController.createOrder(orderDto);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(orderDto);
        verify(orderService, times(1)).createOrder(any(OrderDto.class));
    }

    @Test
    public void testUpdateOrder() {
        OrderDto orderDto = new OrderDto();
        when(orderService.updateOrder(anyInt(), any(OrderDto.class))).thenReturn(orderDto);

        ResponseEntity<OrderDto> response = orderController.updateOrder(1, orderDto);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(orderDto);
        verify(orderService, times(1)).updateOrder(eq(1), any(OrderDto.class));
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderService).deleteOrder(1);

        ResponseEntity<Void> response = orderController.deleteOrder(1);

        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    public void testCreateOrder_ServiceThrowsException() {
        OrderDto orderDto = new OrderDto();
        when(orderService.createOrder(any(OrderDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> orderController.createOrder(orderDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(orderService, times(1)).createOrder(any(OrderDto.class));
    }

    @Test
    public void testUpdateOrder_ServiceThrowsException() {
        OrderDto orderDto = new OrderDto();
        when(orderService.updateOrder(anyInt(), any(OrderDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> orderController.updateOrder(1, orderDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(orderService, times(1)).updateOrder(eq(1), any(OrderDto.class));
    }

    @Test
    public void testDeleteOrder_ServiceThrowsException() {
        doThrow(new RuntimeException("Test Exception")).when(orderService).deleteOrder(1);

        Exception exception = assertThrows(RuntimeException.class, () -> orderController.deleteOrder(1));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(orderService, times(1)).deleteOrder(1);
    }
}
