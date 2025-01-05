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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderItemServiceTest {

    private OrderItemService orderItemService;
    private OrderItemRepository orderItemRepository;
    private OrderItemMapper orderItemMapper;
    private ProductServiceClient productServiceClient;
    private OrderMapper orderMapper;

    @BeforeEach
    public void setUp() {
        orderItemRepository = Mockito.mock(OrderItemRepository.class);
        orderItemMapper = Mockito.mock(OrderItemMapper.class);
        productServiceClient = Mockito.mock(ProductServiceClient.class);
        orderMapper = Mockito.mock(OrderMapper.class);

        // Doğrudan instance oluşturmak yerine mock yaparak inject ediyoruz
        orderItemService = Mockito.spy(new OrderItemService(orderItemRepository, orderItemMapper, orderMapper, productServiceClient));
        Mockito.doReturn(orderItemService).when(orderItemService).getSelf();
    }


    @Test
    public void testAddOrderItem() {
        Order order = new Order();
        order.setId(1);
        order.setOrderItems(new ArrayList<>());
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(2);
        orderItem.setQuantity(3);
        orderItem.setOrder(order);

        when(orderItemMapper.toEntity(any(OrderItemDto.class))).thenReturn(orderItem);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(new OrderItemDto());
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);

        ProductDto productDto = new ProductDto();
        productDto.setPrice(100.0);
        when(productServiceClient.getProductById(2)).thenReturn(productDto);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(2);
        orderItemDto.setQuantity(3);
        OrderItemDto createdOrderItem = orderItemService.addOrderItem(orderDto, orderItemDto);

        assertThat(createdOrderItem).isNotNull();
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    public void testUpdateOrderItem() {
        Order order = new Order();
        order.setId(1);
        order.setOrderItems(new ArrayList<>());
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setProductId(2);
        orderItem.setQuantity(5);
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);

        when(orderItemRepository.findById(1)).thenReturn(Optional.of(orderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(new OrderItemDto());
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);

        ProductDto productDto = new ProductDto();
        productDto.setPrice(100.0);
        when(productServiceClient.getProductById(2)).thenReturn(productDto);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setQuantity(5);
        OrderItemDto updatedOrderItem = orderItemService.updateOrderItem(orderDto, 1, orderItemDto);

        assertThat(updatedOrderItem).isNotNull();
        verify(orderItemRepository, times(1)).findById(1);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    public void testDeleteOrderItem() {
        Order order = new Order();
        order.setId(1);
        order.setOrderItems(new ArrayList<>());
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1);
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);

        when(orderItemRepository.findById(1)).thenReturn(Optional.of(orderItem));
        doNothing().when(orderItemRepository).delete(any(OrderItem.class));
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);

        orderItemService.deleteOrderItem(orderDto, 1);

        verify(orderItemRepository, times(1)).findById(1);
        verify(orderItemRepository, times(1)).delete(any(OrderItem.class));
    }

    @Test
    public void testGetOrderItemsByOrderId() {
        Order order = new Order();
        order.setId(1);
        order.setOrderItems(Collections.singletonList(new OrderItem()));
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);

        when(orderItemMapper.toDto(any(OrderItem.class))).thenReturn(new OrderItemDto());
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(order);

        List<OrderItemDto> orderItems = orderItemService.getOrderItemsByOrderId(orderDto);

        assertThat(orderItems).isNotEmpty();
    }

    @Test
    public void testAddOrderItemWithNullOrderDto() {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(2);
        orderItemDto.setQuantity(3);

        assertThrows(NullPointerException.class, () -> {
            orderItemService.addOrderItem(null, orderItemDto);
        });
    }

    @Test
    public void testHandleQuantityZero() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(1);

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setQuantity(0);  // Miktar 0 veya daha düşük

        when(orderItemRepository.findById(1)).thenReturn(Optional.empty()); // findById çağrısı boş Optional dönecek

        // Beklenen istisnanın fırlatıldığını kontrol et
        assertThrows(RecordNotFoundException.class, () -> {
            orderItemService.updateOrderItem(orderDto, 1, orderItemDto);
        });
    }



    @Test
    public void testFindOrderItemByIdNotFound() {
        when(orderItemRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            orderItemService.findOrderItemById(1);
        });
    }

}
