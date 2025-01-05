package com.example.ProductService.controller;

import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class CatalogControllerTest {

    private CatalogController catalogController;
    private CatalogService catalogService;

    @BeforeEach
    public void setUp() {
        catalogService = Mockito.mock(CatalogService.class);
        catalogController = new CatalogController(catalogService);
    }

    @Test
    public void testGetCatalogById_Success() {
        CatalogDto catalogDto = new CatalogDto();
        when(catalogService.getCatalogById(anyInt())).thenReturn(Optional.of(catalogDto));

        ResponseEntity<CatalogDto> response = catalogController.getCatalogById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(catalogDto);
        verify(catalogService, times(1)).getCatalogById(1);
    }

    @Test
    public void testGetCatalogById_NotFound() {
        when(catalogService.getCatalogById(anyInt())).thenReturn(Optional.empty());

        ResponseEntity<CatalogDto> response = catalogController.getCatalogById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(catalogService, times(1)).getCatalogById(1);
    }

    @Test
    public void testCreateCatalog_Success() {
        CatalogDto catalogDto = new CatalogDto();
        when(catalogService.createCatalog(any(CatalogDto.class))).thenReturn(catalogDto);

        ResponseEntity<CatalogDto> response = catalogController.createCatalog(catalogDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(catalogDto);
        verify(catalogService, times(1)).createCatalog(any(CatalogDto.class));
    }

    @Test
    public void testUpdateCatalog_Success() {
        CatalogDto catalogDto = new CatalogDto();
        when(catalogService.updateCatalog(any(CatalogDto.class))).thenReturn(catalogDto);

        ResponseEntity<CatalogDto> response = catalogController.updateCatalog(1, catalogDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(catalogDto);
        verify(catalogService, times(1)).updateCatalog(any(CatalogDto.class));
    }

    @Test
    public void testDeleteCatalog_Success() {
        doNothing().when(catalogService).deleteCatalog(anyInt());

        ResponseEntity<Void> response = catalogController.deleteCatalog(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(catalogService, times(1)).deleteCatalog(1);
    }

    @Test
    public void testCreateCatalog_ServiceThrowsException() {
        CatalogDto catalogDto = new CatalogDto();
        when(catalogService.createCatalog(any(CatalogDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> catalogController.createCatalog(catalogDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(catalogService, times(1)).createCatalog(any(CatalogDto.class));
    }

    @Test
    public void testUpdateCatalog_ServiceThrowsException() {
        CatalogDto catalogDto = new CatalogDto();
        when(catalogService.updateCatalog(any(CatalogDto.class))).thenThrow(new RuntimeException("Test Exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> catalogController.updateCatalog(1, catalogDto));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(catalogService, times(1)).updateCatalog(any(CatalogDto.class));
    }

    @Test
    public void testDeleteCatalog_ServiceThrowsException() {
        doThrow(new RuntimeException("Test Exception")).when(catalogService).deleteCatalog(1);

        Exception exception = assertThrows(RuntimeException.class, () -> catalogController.deleteCatalog(1));

        assertThat(exception.getMessage()).isEqualTo("Test Exception");
        verify(catalogService, times(1)).deleteCatalog(1);
    }
}
