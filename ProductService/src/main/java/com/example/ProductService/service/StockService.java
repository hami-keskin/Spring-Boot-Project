package com.example.ProductService.service;

import com.example.ProductService.annotation.RequestLogger;
import com.example.ProductService.dto.StockDto;
import com.example.ProductService.entity.Stock;
import com.example.ProductService.mapper.StockMapper;
import com.example.ProductService.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@RequestLogger
public class StockService {
    private final StockRepository stockRepository;
    private final StockMapper stockMapper;

    public Optional<StockDto> getStockById(Integer id) {
        return stockRepository.findById(id)
                .map(stockMapper::toDto);
    }

    public StockDto createStock(StockDto stockDto) {
        Stock stock = stockMapper.toEntity(stockDto);
        stock = stockRepository.save(stock);
        return stockMapper.toDto(stock);
    }

    public StockDto updateStock(StockDto stockDto) {
        Stock stock = stockMapper.toEntity(stockDto);
        stock = stockRepository.save(stock);
        return stockMapper.toDto(stock);
    }

    public void deleteStock(Integer id) {
        stockRepository.deleteById(id);
    }

    @Transactional
    public void reduceStock(Integer productId, Integer quantity) {
        Optional<Stock> optionalStock = stockRepository.findByProductIdWithLock(productId);
        optionalStock.ifPresent(stock -> {
            stock.setQuantity(stock.getQuantity() - quantity);
            stockRepository.save(stock);
        });
    }
}
