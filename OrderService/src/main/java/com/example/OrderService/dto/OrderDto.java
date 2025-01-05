package com.example.OrderService.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Integer id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private Integer status;
}
