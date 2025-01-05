package com.example.ProductService.dto;

import lombok.Data;

@Data
public class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean status;
    private Double price;
    private Integer catalogId;
}
