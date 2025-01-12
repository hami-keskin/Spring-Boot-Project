package com.example.ProductService.service;

import com.example.ProductService.dto.ProductDto;
import com.example.ProductService.entity.Product;
import com.example.ProductService.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Cacheable("product")
    public Optional<ProductDto> getProductById(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    ProductDto productDto = new ProductDto();
                    BeanUtils.copyProperties(product, productDto); // Entity'den DTO'ya kopyalama
                    productDto.setCatalogId(product.getCatalog().getId()); // Catalog ID'yi DTO'ya set et
                    return productDto;
                });
    }

    @CachePut(value = "product", key = "#result.id")
    public ProductDto createProduct(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product); // DTO'dan Entity'ye kopyalama
        product = productRepository.save(product);

        ProductDto resultDto = new ProductDto();
        BeanUtils.copyProperties(product, resultDto); // Entity'den DTO'ya kopyalama
        resultDto.setCatalogId(product.getCatalog().getId()); // Catalog ID'yi DTO'ya set et
        return resultDto;
    }

    @CachePut(value = "product", key = "#productDto.id")
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product); // DTO'dan Entity'ye kopyalama
        product = productRepository.save(product);

        ProductDto resultDto = new ProductDto();
        BeanUtils.copyProperties(product, resultDto); // Entity'den DTO'ya kopyalama
        resultDto.setCatalogId(product.getCatalog().getId()); // Catalog ID'yi DTO'ya set et
        return resultDto;
    }

    @CacheEvict(value = "product", key = "#id")
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Cacheable("productsByCatalog")
    public List<ProductDto> getProductsByCatalogId(Integer catalogId) {
        return productRepository.findByCatalogId(catalogId)
                .stream()
                .map(product -> {
                    ProductDto productDto = new ProductDto();
                    BeanUtils.copyProperties(product, productDto); // Entity'den DTO'ya kopyalama
                    productDto.setCatalogId(product.getCatalog().getId()); // Catalog ID'yi DTO'ya set et
                    return productDto;
                })
                .collect(Collectors.toList());
    }
}