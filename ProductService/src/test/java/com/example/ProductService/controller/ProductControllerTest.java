package com.example.ProductService.controller;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    private ProductController productController;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = Mockito.mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    public void testGetProductById_Success() {
        ProductDto productDto = new ProductDto();
        when(productService.getProductById(anyInt())).thenReturn(Optional.of(productDto));

        ResponseEntity<ProductDto> response = productController.getProductById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(productDto);
        verify(productService, times(1)).getProductById(1);
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productService.getProductById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<ProductDto> response = productController.getProductById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(productService, times(1)).getProductById(1);
    }

    @Test
    public void testCreateProduct_Success() {
        ProductDto productDto = new ProductDto();
        when(productService.createProduct(any(ProductDto.class))).thenReturn(productDto);

        ResponseEntity<ProductDto> response = productController.createProduct(productDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(productDto);
        verify(productService, times(1)).createProduct(any(ProductDto.class));
    }

    @Test
    public void testUpdateProduct_Success() {
        ProductDto productDto = new ProductDto();
        when(productService.updateProduct(any(ProductDto.class))).thenReturn(productDto);

        ResponseEntity<ProductDto> response = productController.updateProduct(1, productDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(productDto);
        verify(productService, times(1)).updateProduct(any(ProductDto.class));
    }

    @Test
    public void testDeleteProduct_Success() {
        doNothing().when(productService).deleteProduct(anyInt());

        ResponseEntity<Void> response = productController.deleteProduct(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService, times(1)).deleteProduct(1);
    }

    @Test
    public void testGetProductsByCatalogId_Success() {
        List<ProductDto> productDtos = Arrays.asList(new ProductDto(), new ProductDto());
        when(productService.getProductsByCatalogId(anyInt())).thenReturn(productDtos);

        ResponseEntity<List<ProductDto>> response = productController.getProductsByCatalogId(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(productDtos);
        verify(productService, times(1)).getProductsByCatalogId(1);
    }

    @Test
    public void testCreateProduct_ServiceThrowsException() {
        ProductDto productDto = new ProductDto();
        when(productService.createProduct(any(ProductDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> productController.createProduct(productDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(productService, times(1)).createProduct(any(ProductDto.class));
    }

    @Test
    public void testUpdateProduct_ServiceThrowsException() {
        ProductDto productDto = new ProductDto();
        when(productService.updateProduct(any(ProductDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> productController.updateProduct(1, productDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(productService, times(1)).updateProduct(any(ProductDto.class));
    }

    @Test
    public void testDeleteProduct_ServiceThrowsException() {
        doThrow(new RuntimeException("Test Exception")).when(productService).deleteProduct(1);

        Exception exception = assertThrows(RuntimeException.class, () -> productController.deleteProduct(1));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(productService, times(1)).deleteProduct(1);
    }
}
