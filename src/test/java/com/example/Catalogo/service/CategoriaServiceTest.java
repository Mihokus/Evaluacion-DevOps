package com.example.Catalogo.service;

import com.example.Catalogo.dto.CategoriaDTO;
import com.example.Catalogo.entity.Categoria;
import com.example.Catalogo.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void listarTodas_retornaListaDeCategorias() {
        Categoria c1 = new Categoria(1L, "Frutas", "Frutas frescas");
        Categoria c2 = new Categoria(2L, "Verduras", "Verduras frescas");
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<CategoriaDTO> resultado = categoriaService.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Frutas", resultado.get(0).getNombre());
        assertEquals("Verduras", resultado.get(1).getNombre());
    }

    @Test
    void listarTodas_cuandoNoHayCategorias_retornaListaVacia() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<CategoriaDTO> resultado = categoriaService.listarTodas();

        assertTrue(resultado.isEmpty());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void crear_cuandoNombreNoExiste_creaCorrectamente() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Lácteos");
        dto.setDescripcion("Productos lácteos");

        Categoria entidad = new Categoria(1L, "Lácteos", "Productos lácteos");

        when(categoriaRepository.existsByNombre("Lácteos")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(entidad);

        CategoriaDTO resultado = categoriaService.crear(dto);

        assertNotNull(resultado);
        assertEquals("Lácteos", resultado.getNombre());
        assertEquals("Productos lácteos", resultado.getDescripcion());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void crear_cuandoNombreYaExiste_lanzaExcepcion() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Frutas");

        when(categoriaRepository.existsByNombre("Frutas")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoriaService.crear(dto));
        assertTrue(ex.getMessage().contains("La categoría ya existe"));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void actualizar_cuandoCategoriaExiste_actualizaCorrectamente() {
        Categoria existente = new Categoria(1L, "Frutas", "Frutas frescas");
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Frutas Tropicales");
        dto.setDescripcion("Frutas tropicales frescas");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(existente);

        CategoriaDTO resultado = categoriaService.actualizar(1L, dto);

        assertEquals("Frutas Tropicales", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(existente);
    }

    @Test
    void actualizar_cuandoCategoriaNoExiste_lanzaExcepcion() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("No existe");

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoriaService.actualizar(99L, dto));
        assertTrue(ex.getMessage().contains("Categoría no encontrada"));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoCategoriaExiste_eliminaCorrectamente() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoriaService.eliminar(1L));
        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoCategoriaNoExiste_lanzaExcepcion() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoriaService.eliminar(99L));
        assertTrue(ex.getMessage().contains("Categoría no encontrada"));
        verify(categoriaRepository, never()).deleteById(any());
    }
}
