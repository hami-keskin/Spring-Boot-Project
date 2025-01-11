package com.example.ProductService.service;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.entity.Catalog;
import com.example.ProductService.entity.Product;
import com.example.ProductService.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableCaching
@Import(ProductService.class) // ProductService'i Spring context'e ekler
public class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        // Cache'i temizle
        cacheManager.getCache("product").clear();
        cacheManager.getCache("productsByCatalog").clear();
    }

    @Test
    public void testGetProductById() {
        // Arrange
        Catalog catalog = new Catalog();
        catalog.setId(1); // Catalog nesnesi oluşturuldu

        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setCatalog(catalog); // Catalog nesnesi atandı

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Act
        Optional<ProductDto> result = productService.getProductById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getName()).isEqualTo("Test Product");
        assertThat(result.get().getDescription()).isEqualTo("Test Description");
        assertThat(result.get().getPrice()).isEqualTo(100.0);
        assertThat(result.get().getCatalogId()).isEqualTo(1); // CatalogId kontrolü

        verify(productRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        Catalog catalog = new Catalog();
        catalog.setId(1); // Catalog nesnesi oluşturuldu

        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setDescription("Test Description");
        productDto.setPrice(100.0);
        productDto.setCatalogId(1);

        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setCatalog(catalog); // Catalog nesnesi atandı

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto result = productService.createProduct(productDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getPrice()).isEqualTo(100.0);
        assertThat(result.getCatalogId()).isEqualTo(1);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct() {
        // Arrange
        Catalog catalog = new Catalog();
        catalog.setId(1); // Catalog nesnesi oluşturuldu

        ProductDto productDto = new ProductDto();
        productDto.setId(1);
        productDto.setName("Updated Product");
        productDto.setDescription("Updated Description");
        productDto.setPrice(150.0);
        productDto.setCatalogId(1);

        Product product = new Product();
        product.setId(1);
        product.setName("Updated Product");
        product.setDescription("Updated Description");
        product.setPrice(150.0);
        product.setCatalog(catalog); // Catalog nesnesi atandı

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto result = productService.updateProduct(productDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPrice()).isEqualTo(150.0);
        assertThat(result.getCatalogId()).isEqualTo(1);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testDeleteProduct() {
        // Arrange
        doNothing().when(productRepository).deleteById(1);

        // Act
        productService.deleteProduct(1);

        // Assert
        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    public void testGetProductsByCatalogId() {
        // Arrange
        Catalog catalog = new Catalog();
        catalog.setId(1); // Catalog nesnesi oluşturuldu

        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(100.0);
        product1.setCatalog(catalog); // Catalog nesnesi atandı

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(200.0);
        product2.setCatalog(catalog); // Catalog nesnesi atandı

        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findByCatalogId(1)).thenReturn(products);

        // Act
        List<ProductDto> result = productService.getProductsByCatalogId(1);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Product 1");
        assertThat(result.get(0).getDescription()).isEqualTo("Description 1");
        assertThat(result.get(0).getPrice()).isEqualTo(100.0);
        assertThat(result.get(0).getCatalogId()).isEqualTo(1);

        assertThat(result.get(1).getId()).isEqualTo(2);
        assertThat(result.get(1).getName()).isEqualTo("Product 2");
        assertThat(result.get(1).getDescription()).isEqualTo("Description 2");
        assertThat(result.get(1).getPrice()).isEqualTo(200.0);
        assertThat(result.get(1).getCatalogId()).isEqualTo(1);

        verify(productRepository, times(1)).findByCatalogId(1);
    }
}