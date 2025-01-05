package com.example.ProductService.service;

import com.example.ProductService.annotation.RequestLogger;
import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.entity.Catalog;
import com.example.ProductService.mapper.CatalogMapper;
import com.example.ProductService.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@RequestLogger
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;

    @Cacheable("catalog")
    public Optional<CatalogDto> getCatalogById(Integer id) {
        return catalogRepository.findById(id)
                .map(catalogMapper::toDto);
    }

    @CachePut(value = "catalog", key = "#result.id")
    public CatalogDto createCatalog(CatalogDto catalogDto) {
        Catalog catalog = catalogMapper.toEntity(catalogDto);
        catalog = catalogRepository.save(catalog);
        return catalogMapper.toDto(catalog);
    }

    @CachePut(value = "catalog", key = "#catalogDto.id")
    public CatalogDto updateCatalog(CatalogDto catalogDto) {
        Catalog catalog = catalogMapper.toEntity(catalogDto);
        catalog = catalogRepository.save(catalog);
        return catalogMapper.toDto(catalog);
    }

    @CacheEvict(value = "catalog", key = "#id")
    public void deleteCatalog(Integer id) {
        catalogRepository.deleteById(id);
    }
}
