package com.example.ProductService.service;

import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.entity.Catalog;
import com.example.ProductService.repository.CatalogRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;

    @Cacheable("catalog")
    public Optional<CatalogDto> getCatalogById(Integer id) {
        return catalogRepository.findById(id)
                .map(catalog -> {
                    CatalogDto catalogDto = new CatalogDto();
                    BeanUtils.copyProperties(catalog, catalogDto); // Entity'den DTO'ya kopyalama
                    return catalogDto;
                });
    }

    @CachePut(value = "catalog", key = "#result.id")
    public CatalogDto createCatalog(CatalogDto catalogDto) {
        Catalog catalog = new Catalog();
        BeanUtils.copyProperties(catalogDto, catalog); // DTO'dan Entity'ye kopyalama
        catalog = catalogRepository.save(catalog);

        CatalogDto resultDto = new CatalogDto();
        BeanUtils.copyProperties(catalog, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    @CachePut(value = "catalog", key = "#catalogDto.id")
    public CatalogDto updateCatalog(CatalogDto catalogDto) {
        Catalog catalog = new Catalog();
        BeanUtils.copyProperties(catalogDto, catalog); // DTO'dan Entity'ye kopyalama
        catalog = catalogRepository.save(catalog);

        CatalogDto resultDto = new CatalogDto();
        BeanUtils.copyProperties(catalog, resultDto); // Entity'den DTO'ya kopyalama
        return resultDto;
    }

    @CacheEvict(value = "catalog", key = "#id")
    public void deleteCatalog(Integer id) {
        catalogRepository.deleteById(id);
    }
}