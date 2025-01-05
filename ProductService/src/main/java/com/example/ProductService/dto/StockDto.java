package com.example.ProductService.dto;

import lombok.Data;

@Data
public class StockDto {
    private Integer id;
    private Integer quantity;
    private Integer productId;
}
