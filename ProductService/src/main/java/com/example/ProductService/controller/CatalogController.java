package com.example.ProductService.controller;

import com.example.ProductService.dto.CatalogDto;
import com.example.ProductService.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/catalogs")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @GetMapping("/{id}")
    public ResponseEntity<CatalogDto> getCatalogById(@PathVariable Integer id) {
        Optional<CatalogDto> catalogDto = catalogService.getCatalogById(id);
        return catalogDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CatalogDto> createCatalog(@RequestBody CatalogDto catalogDto) {
        CatalogDto createdCatalog = catalogService.createCatalog(catalogDto);
        return ResponseEntity.ok(createdCatalog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogDto> updateCatalog(@PathVariable Integer id, @RequestBody CatalogDto catalogDto) {
        catalogDto.setId(id);
        CatalogDto updatedCatalog = catalogService.updateCatalog(catalogDto);
        return ResponseEntity.ok(updatedCatalog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable Integer id) {
        catalogService.deleteCatalog(id);
        return ResponseEntity.noContent().build();
    }
}
