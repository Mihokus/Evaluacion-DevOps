package com.example.Catalogo.controller;

import com.example.Catalogo.dto.CategoriaDTO;
import com.example.Catalogo.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    @Test
    void listarTodas_retornaLista() {
        CategoriaDTO c1 = new CategoriaDTO();
        c1.setNombre("Frutas");
        CategoriaDTO c2 = new CategoriaDTO();
        c2.setNombre("Verduras");

        when(categoriaService.listarTodas()).thenReturn(Arrays.asList(c1, c2));

        List<CategoriaDTO> resultado = categoriaController.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Frutas", resultado.get(0).getNombre());
        verify(categoriaService, times(1)).listarTodas();
    }

    @Test
    void crear_retornaCategoriaCreada() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Lácteos");

        when(categoriaService.crear(any(CategoriaDTO.class))).thenReturn(dto);

        CategoriaDTO resultado = categoriaController.crear(dto);

        assertNotNull(resultado);
        assertEquals("Lácteos", resultado.getNombre());
        verify(categoriaService, times(1)).crear(dto);
    }

    @Test
    void actualizar_retornaCategoriaActualizada() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Frutas Tropicales");

        when(categoriaService.actualizar(eq(1L), any(CategoriaDTO.class))).thenReturn(dto);

        CategoriaDTO resultado = categoriaController.actualizar(1L, dto);

        assertNotNull(resultado);
        assertEquals("Frutas Tropicales", resultado.getNombre());
        verify(categoriaService, times(1)).actualizar(1L, dto);
    }

    @Test
    void eliminar_llamaAlServicio() {
        doNothing().when(categoriaService).eliminar(1L);

        categoriaController.eliminar(1L);

        verify(categoriaService, times(1)).eliminar(1L);
    }
}