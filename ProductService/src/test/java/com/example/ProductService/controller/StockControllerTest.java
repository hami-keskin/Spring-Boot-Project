package com.example.ProductService.controller;

import com.example.ProductService.dto.StockDto;
import com.example.ProductService.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class StockControllerTest {

    private StockController stockController;
    private StockService stockService;

    @BeforeEach
    public void setUp() {
        stockService = Mockito.mock(StockService.class);
        stockController = new StockController(stockService);
    }

    @Test
    public void testGetStockById_Success() {
        StockDto stockDto = new StockDto();
        when(stockService.getStockById(anyInt())).thenReturn(Optional.of(stockDto));

        ResponseEntity<StockDto> response = stockController.getStockById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(stockDto);
        verify(stockService, times(1)).getStockById(1);
    }

    @Test
    public void testGetStockById_NotFound() {
        when(stockService.getStockById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<StockDto> response = stockController.getStockById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(stockService, times(1)).getStockById(1);
    }

    @Test
    public void testCreateStock_Success() {
        StockDto stockDto = new StockDto();
        when(stockService.createStock(any(StockDto.class))).thenReturn(stockDto);

        ResponseEntity<StockDto> response = stockController.createStock(stockDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(stockDto);
        verify(stockService, times(1)).createStock(any(StockDto.class));
    }

    @Test
    public void testUpdateStock_Success() {
        StockDto stockDto = new StockDto();
        when(stockService.updateStock(any(StockDto.class))).thenReturn(stockDto);

        ResponseEntity<StockDto> response = stockController.updateStock(1, stockDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(stockDto);
        verify(stockService, times(1)).updateStock(any(StockDto.class));
    }

    @Test
    public void testDeleteStock_Success() {
        doNothing().when(stockService).deleteStock(anyInt());

        ResponseEntity<Void> response = stockController.deleteStock(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(stockService, times(1)).deleteStock(1);
    }

    @Test
    public void testReduceStock_Success() {
        doNothing().when(stockService).reduceStock(anyInt(), anyInt());

        ResponseEntity<Void> response = stockController.reduceStock(1, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(stockService, times(1)).reduceStock(1, 10);
    }

    @Test
    public void testCreateStock_ServiceThrowsException() {
        StockDto stockDto = new StockDto();
        when(stockService.createStock(any(StockDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> stockController.createStock(stockDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(stockService, times(1)).createStock(any(StockDto.class));
    }

    @Test
    public void testUpdateStock_ServiceThrowsException() {
        StockDto stockDto = new StockDto();
        when(stockService.updateStock(any(StockDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> stockController.updateStock(1, stockDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(stockService, times(1)).updateStock(any(StockDto.class));
    }

    @Test
    public void testDeleteStock_ServiceThrowsException() {
        doThrow(new RuntimeException("Test Exception")).when(stockService).deleteStock(1);

        Exception exception = assertThrows(RuntimeException.class, () -> stockController.deleteStock(1));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(stockService, times(1)).deleteStock(1);
    }

    @Test
    public void testReduceStock_ServiceThrowsException() {
        doThrow(new RuntimeException("Test Exception")).when(stockService).reduceStock(1, 10);

        Exception exception = assertThrows(RuntimeException.class, () -> stockController.reduceStock(1, 10));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(stockService, times(1)).reduceStock(1, 10);
    }
}
