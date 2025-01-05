package com.example.ProductService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;
    private Boolean status;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    private Catalog catalog;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Stock stock;
}
