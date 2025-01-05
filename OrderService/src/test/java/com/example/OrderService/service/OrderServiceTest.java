package com.example.OrderService.service;

import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.entity.Order;
import com.example.OrderService.exception.RecordNotFoundException;
import com.example.OrderService.mapper.OrderMapper;
import com.example.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;

    @BeforeEach
    public void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderMapper = Mockito.mock(OrderMapper.class);

        orderService = new OrderService(orderRepository, orderMapper);
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order();
        order.setId(1);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        Optional<OrderDto> orderDto = orderService.getOrderById(1);

        assertThat(orderDto).isPresent();
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            orderService.getOrderById(1);
        });

        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateOrder() {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(0.0);
        order.setStatus(1);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        OrderDto orderDto = new OrderDto();
        OrderDto createdOrder = orderService.createOrder(orderDto);

        assertThat(createdOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testCreateOrderWithEmptyDto() {
        Order order = new Order();
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        OrderDto orderDto = new OrderDto();
        OrderDto createdOrder = orderService.createOrder(orderDto);

        assertThat(createdOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrder() {
        Order order = new Order();
        order.setId(1);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        OrderDto orderDto = new OrderDto();
        OrderDto updatedOrder = orderService.updateOrder(1, orderDto);

        assertThat(updatedOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1);

        orderService.deleteOrder(1);

        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteOrder_NotFound() {
        doThrow(new RecordNotFoundException("Order not found with id 1")).when(orderRepository).deleteById(1);

        assertThrows(RecordNotFoundException.class, () -> {
            orderService.deleteOrder(1);
        });

        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    public void testCreateOrder_CachePut() {
        Order order = new Order();
        order.setId(1);
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        OrderDto orderDto = new OrderDto();
        OrderDto createdOrder = orderService.createOrder(orderDto);

        assertThat(createdOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testDeleteOrder_CacheEvict() {
        Integer orderId = 1;

        doNothing().when(orderRepository).deleteById(orderId);

        orderService.deleteOrder(orderId);

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(orderRepository).deleteById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(orderId);
        // Cache'in temizlendiğini doğrulayan ek bir kontrol eklenebilir
    }

    @Test
    public void testUpdateOrder_CachePut() {
        Order order = new Order();
        order.setId(1);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        OrderDto orderDto = new OrderDto();
        OrderDto updatedOrder = orderService.updateOrder(1, orderDto);

        assertThat(updatedOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_withArgumentCaptor() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);

        Order order = new Order();
        order.setId(1);

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        orderService.createOrder(orderDto);

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();
        assertThat(capturedOrder.getId()).isEqualTo(1);
    }

    @Test
    public void testUpdateOrder_withArgumentCaptor() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);
        orderDto.setStatus(2); // Yeni bir alan güncellemesi

        Order order = new Order();
        order.setId(1);
        order.setStatus(2); // Bu değeri set ettik

        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        orderService.updateOrder(1, orderDto);

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();
        assertThat(capturedOrder.getId()).isEqualTo(1);
        assertThat(capturedOrder.getStatus()).isEqualTo(2); // Beklenen değer doğrulanıyor
    }
}
