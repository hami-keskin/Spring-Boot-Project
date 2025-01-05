package com.example.ProductService.dto;

import lombok.Data;

@Data
public class CatalogDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean status;
}
