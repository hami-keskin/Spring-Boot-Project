package com.example.ProductService.service;

import com.example.ProductService.dto.StockDto;
import com.example.ProductService.entity.Stock;
import com.example.ProductService.repository.StockRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public Optional<StockDto> getStockById(Integer id) {
        return stockRepository.findById(id)
                .map(stock -> {
                    StockDto stockDto = new StockDto();
                    BeanUtils.copyProperties(stock, stockDto); // Entity'den DTO'ya kopyalama
                    return stockDto;
                });
    }

    public StockDto createStock(StockDto stockDto) {
        Stock stock = new Stock();
        BeanUtils.copyProperties(stockDto, stock); // DTO'dan Entity'ye kopyalama
        stock = stockRepository.save(stock);

        StockDto resultDto = new StockDto();
        BeanUtils.copyProperties(stock, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    public StockDto updateStock(StockDto stockDto) {
        Stock stock = new Stock();
        BeanUtils.copyProperties(stockDto, stock); // DTO'dan Entity'ye kopyalama
        stock = stockRepository.save(stock);

        StockDto resultDto = new StockDto();
        BeanUtils.copyProperties(stock, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
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