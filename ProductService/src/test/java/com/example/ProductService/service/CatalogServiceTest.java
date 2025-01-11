package com.example.ProductService.service;

import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.entity.Catalog;
import com.example.ProductService.repository.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableCaching
@Import(CatalogService.class) // CatalogService'i Spring context'e ekler
public class CatalogServiceTest {

    @MockBean
    private CatalogRepository catalogRepository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        // Cache'i temizle
        cacheManager.getCache("catalog").clear();
    }

    @Test
    public void testGetCatalogById() {
        // Arrange
        Catalog catalog = new Catalog();
        catalog.setId(1);
        catalog.setName("Test Catalog");
        catalog.setDescription("Test Description");

        when(catalogRepository.findById(1)).thenReturn(Optional.of(catalog));

        // Act
        Optional<CatalogDto> result = catalogService.getCatalogById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getName()).isEqualTo("Test Catalog");
        assertThat(result.get().getDescription()).isEqualTo("Test Description");

        verify(catalogRepository, times(1)).findById(1);
    }

    @Test
    public void testCreateCatalog() {
        // Arrange
        CatalogDto catalogDto = new CatalogDto();
        catalogDto.setName("Test Catalog");
        catalogDto.setDescription("Test Description");

        Catalog catalog = new Catalog();
        catalog.setId(1);
        catalog.setName("Test Catalog");
        catalog.setDescription("Test Description");

        when(catalogRepository.save(any(Catalog.class))).thenReturn(catalog);

        // Act
        CatalogDto result = catalogService.createCatalog(catalogDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Test Catalog");
        assertThat(result.getDescription()).isEqualTo("Test Description");

        verify(catalogRepository, times(1)).save(any(Catalog.class));
    }

    @Test
    public void testUpdateCatalog() {
        // Arrange
        CatalogDto catalogDto = new CatalogDto();
        catalogDto.setId(1);
        catalogDto.setName("Updated Catalog");
        catalogDto.setDescription("Updated Description");

        Catalog catalog = new Catalog();
        catalog.setId(1);
        catalog.setName("Updated Catalog");
        catalog.setDescription("Updated Description");

        when(catalogRepository.save(any(Catalog.class))).thenReturn(catalog);

        // Act
        CatalogDto result = catalogService.updateCatalog(catalogDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Updated Catalog");
        assertThat(result.getDescription()).isEqualTo("Updated Description");

        verify(catalogRepository, times(1)).save(any(Catalog.class));
    }

    @Test
    public void testDeleteCatalog() {
        // Arrange
        doNothing().when(catalogRepository).deleteById(1);

        // Act
        catalogService.deleteCatalog(1);

        // Assert
        verify(catalogRepository, times(1)).deleteById(1);
    }
}