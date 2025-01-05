package com.example.ProductService.service;

import com.example.ProductService.annotation.RequestLogger;
import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.entity.Product;
import com.example.ProductService.mapper.ProductMapper;
import com.example.ProductService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RequestLogger
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable("product")
    public Optional<ProductDto> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    @CachePut(value = "product", key = "#result.id")
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @CachePut(value = "product", key = "#productDto.id")
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @CacheEvict(value = "product", key = "#id")
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Cacheable("productsByCatalog")
    public List<ProductDto> getProductsByCatalogId(Integer catalogId) {
        return productRepository.findByCatalogId(catalogId)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}
