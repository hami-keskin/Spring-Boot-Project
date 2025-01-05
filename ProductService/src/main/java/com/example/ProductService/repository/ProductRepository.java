package com.example.ProductService.repository;

import com.example.ProductService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCatalogId(Integer catalogId);
}
