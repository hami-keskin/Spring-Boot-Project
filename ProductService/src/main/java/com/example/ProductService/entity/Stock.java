package com.example.ProductService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY) // Lazy loading eklendi
    @JoinColumn(name = "product_id")
    private Product product;
}