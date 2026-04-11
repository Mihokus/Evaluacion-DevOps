package com.example.Catalogo.controller;
import com.example.Catalogo.dto.CategoriaDTO;
import com.example.Catalogo.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor

public class CategoriaController {

    private final CategoriaService categoriaService;

    
    @GetMapping
    public List<CategoriaDTO> listarTodas() {
        return categoriaService.listarTodas();
    }

    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaDTO crear(@Valid @RequestBody CategoriaDTO dto) {
        return categoriaService.crear(dto);
    }

   
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaDTO actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        return categoriaService.actualizar(id, dto);
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }
}
