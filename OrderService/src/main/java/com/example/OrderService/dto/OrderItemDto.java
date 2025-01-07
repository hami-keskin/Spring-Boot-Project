package com.example.OrderService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Double price;
    private Double totalAmount;
}
