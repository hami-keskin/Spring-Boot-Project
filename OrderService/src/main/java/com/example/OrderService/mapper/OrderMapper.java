package com.example.OrderService.mapper;

import com.example.OrderService.dto.OrderDto;
import com.example.OrderService.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
    Order toEntity(OrderDto orderDto);
}
