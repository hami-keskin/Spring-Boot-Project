package com.example.ProductService.mapper;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "catalog.id", target = "catalogId")
    ProductDto toDto(Product product);

    @Mapping(source = "catalogId", target = "catalog.id")
    Product toEntity(ProductDto productDto);
}
