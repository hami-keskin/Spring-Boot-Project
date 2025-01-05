package com.example.ProductService.controller;

import com.example.ProductService.dto.StockDto;
import com.example.ProductService.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getStockById(@PathVariable Integer id) {
        Optional<StockDto> stockDto = stockService.getStockById(id);
        return stockDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockDto> createStock(@RequestBody StockDto stockDto) {
        StockDto createdStock = stockService.createStock(stockDto);
        return ResponseEntity.ok(createdStock);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDto> updateStock(@PathVariable Integer id, @RequestBody StockDto stockDto) {
        stockDto.setId(id);
        StockDto updatedStock = stockService.updateStock(stockDto);
        return ResponseEntity.ok(updatedStock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Integer id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reduce")
    public ResponseEntity<Void> reduceStock(@PathVariable Integer id, @RequestParam Integer quantity) {
        stockService.reduceStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}
