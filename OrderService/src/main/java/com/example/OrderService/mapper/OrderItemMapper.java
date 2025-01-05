package com.example.OrderService.mapper;

import com.example.OrderService.dto.OrderItemDto;
import com.example.OrderService.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "order.id", target = "orderId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(source = "orderId", target = "order.id")
    OrderItem toEntity(OrderItemDto orderItemDto);
}
