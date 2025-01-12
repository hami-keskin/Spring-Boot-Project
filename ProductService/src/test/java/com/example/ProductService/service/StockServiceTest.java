package com.example.ProductService.service;

import com.example.ProductService.dto.StockDto;
import com.example.ProductService.entity.Product;
import com.example.ProductService.entity.Stock;
import com.example.ProductService.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class StockServiceTest {

    @MockBean
    private StockRepository stockRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        // Cache'i temizle
        cacheManager.getCache("stock").clear();
    }

    @Test
    public void testGetStockById() {
        // Arrange
        Product product = new Product();
        product.setId(101); // Product nesnesi oluşturuldu

        Stock stock = new Stock();
        stock.setId(1);
        stock.setProduct(product); // Product nesnesi atandı
        stock.setQuantity(50);

        when(stockRepository.findById(1)).thenReturn(Optional.of(stock));

        // Act
        Optional<StockDto> result = stockService.getStockById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getProductId()).isEqualTo(101); // Product ID kontrolü
        assertThat(result.get().getQuantity()).isEqualTo(50);

        verify(stockRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateStock() {
        // Arrange
        StockDto stockDto = new StockDto();
        stockDto.setProductId(101); // Product ID'si
        stockDto.setQuantity(50);

        Product product = new Product();
        product.setId(101); // Product nesnesi oluşturuldu

        Stock stock = new Stock();
        stock.setId(1);
        stock.setProduct(product); // Product nesnesi atandı
        stock.setQuantity(50);

        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        StockDto result = stockService.createStock(stockDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getProductId()).isEqualTo(101); // Product ID kontrolü
        assertThat(result.getQuantity()).isEqualTo(50);

        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    public void testUpdateStock() {
        // Arrange
        StockDto stockDto = new StockDto();
        stockDto.setId(1);
        stockDto.setProductId(101); // Product ID'si
        stockDto.setQuantity(100);

        Product product = new Product();
        product.setId(101); // Product nesnesi oluşturuldu

        Stock stock = new Stock();
        stock.setId(1);
        stock.setProduct(product); // Product nesnesi atandı
        stock.setQuantity(100);

        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        StockDto result = stockService.updateStock(stockDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getProductId()).isEqualTo(101); // Product ID kontrolü
        assertThat(result.getQuantity()).isEqualTo(100);

        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    public void testDeleteStock() {
        // Arrange
        doNothing().when(stockRepository).deleteById(1);

        // Act
        stockService.deleteStock(1);

        // Assert
        verify(stockRepository, times(1)).deleteById(1);
    }

    @Test
    public void testReduceStock() {
        // Arrange
        Product product = new Product();
        product.setId(101); // Product nesnesi oluşturuldu

        Stock stock = new Stock();
        stock.setId(1);
        stock.setProduct(product); // Product nesnesi atandı
        stock.setQuantity(50);

        when(stockRepository.findByProductIdWithLock(101)).thenReturn(Optional.of(stock));

        // Act
        stockService.reduceStock(101, 10);

        // Assert
        assertThat(stock.getQuantity()).isEqualTo(40); // 50 - 10 = 40
        verify(stockRepository, times(1)).findByProductIdWithLock(101);
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    public void testReduceStock_NotFound() {
        // Arrange
        when(stockRepository.findByProductIdWithLock(101)).thenReturn(Optional.empty());

        // Act
        stockService.reduceStock(101, 10);

        // Assert
        verify(stockRepository, times(1)).findByProductIdWithLock(101);
        verify(stockRepository, never()).save(any(Stock.class)); // Stok bulunamadığı için save çağrılmamalı
    }
}