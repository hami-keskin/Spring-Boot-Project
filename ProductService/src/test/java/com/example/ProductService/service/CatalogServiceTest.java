package com.example.ProductService.service;

import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.entity.Catalog;
import com.example.ProductService.mapper.CatalogMapper;
import com.example.ProductService.repository.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CatalogServiceTest {

    @Mock
    private CatalogRepository catalogRepository;

    @Mock
    private CatalogMapper catalogMapper;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        catalogService = new CatalogService(catalogRepository, catalogMapper);
    }

    @Test
    void testGetCatalogById() {
        Catalog catalog = new Catalog();
        CatalogDto catalogDto = new CatalogDto();
        when(catalogRepository.findById(1)).thenReturn(Optional.of(catalog));
        when(catalogMapper.toDto(catalog)).thenReturn(catalogDto);

        Optional<CatalogDto> result = catalogService.getCatalogById(1);
        assertTrue(result.isPresent());
        assertEquals(catalogDto, result.get());
    }

    @Test
    void testCreateCatalog() {
        Catalog catalog = new Catalog();
        CatalogDto catalogDto = new CatalogDto();
        when(catalogMapper.toEntity(catalogDto)).thenReturn(catalog);
        when(catalogRepository.save(catalog)).thenReturn(catalog);
        when(catalogMapper.toDto(catalog)).thenReturn(catalogDto);

        CatalogDto result = catalogService.createCatalog(catalogDto);
        assertEquals(catalogDto, result);
    }

    @Test
    void testUpdateCatalog() {
        Catalog catalog = new Catalog();
        CatalogDto catalogDto = new CatalogDto();
        when(catalogMapper.toEntity(catalogDto)).thenReturn(catalog);
        when(catalogRepository.save(catalog)).thenReturn(catalog);
        when(catalogMapper.toDto(catalog)).thenReturn(catalogDto);

        CatalogDto result = catalogService.updateCatalog(catalogDto);
        assertEquals(catalogDto, result);
    }

    @Test
    void testDeleteCatalog() {
        doNothing().when(catalogRepository).deleteById(1);
        catalogService.deleteCatalog(1);
        verify(catalogRepository, times(1)).deleteById(1);
    }
}
