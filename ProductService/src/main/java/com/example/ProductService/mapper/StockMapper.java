package com.example.ProductService.mapper;

import com.example.ProductService.dto.StockDto;
import com.example.ProductService.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {
    @Mapping(source = "product.id", target = "productId")
    StockDto toDto(Stock stock);

    @Mapping(source = "productId", target = "product.id")
    Stock toEntity(StockDto stockDto);
}
