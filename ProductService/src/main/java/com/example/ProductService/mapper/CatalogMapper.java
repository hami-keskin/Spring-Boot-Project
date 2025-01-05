package com.example.ProductService.mapper;

import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.entity.Catalog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CatalogMapper {
    CatalogDto toDto(Catalog catalog);
    Catalog toEntity(CatalogDto catalogDto);
}
