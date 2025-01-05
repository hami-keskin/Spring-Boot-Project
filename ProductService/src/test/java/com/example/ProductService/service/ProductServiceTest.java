package com.example.ProductService.service;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.entity.Product;
import com.example.ProductService.mapper.ProductMapper;
import com.example.ProductService.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository, productMapper);
    }

    @Test
    void testGetProductById() {
        Product product = new Product();
        ProductDto productDto = new ProductDto();
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        Optional<ProductDto> result = productService.getProductById(1);
        assertTrue(result.isPresent());
        assertEquals(productDto, result.get());
    }

    @Test
    void testCreateProduct() {
        Product product = new Product();
        ProductDto productDto = new ProductDto();
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.createProduct(productDto);
        assertEquals(productDto, result);
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product();
        ProductDto productDto = new ProductDto();
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);

        ProductDto result = productService.updateProduct(productDto);
        assertEquals(productDto, result);
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1);
        productService.deleteProduct(1);
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetProductsByCatalogId() {
        Product product1 = new Product();
        Product product2 = new Product();
        ProductDto productDto1 = new ProductDto();
        ProductDto productDto2 = new ProductDto();
        List<Product> products = Stream.of(product1, product2).collect(Collectors.toList());
        List<ProductDto> productDtos = Stream.of(productDto1, productDto2).collect(Collectors.toList());

        when(productRepository.findByCatalogId(1)).thenReturn(products);
        when(productMapper.toDto(product1)).thenReturn(productDto1);
        when(productMapper.toDto(product2)).thenReturn(productDto2);

        List<ProductDto> result = productService.getProductsByCatalogId(1);
        assertEquals(productDtos, result);
    }
}
