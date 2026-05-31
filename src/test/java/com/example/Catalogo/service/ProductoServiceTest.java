package com.example.Catalogo.service;
import com.example.Catalogo.dto.ProductoRequest;
import com.example.Catalogo.entity.Categoria;
import com.example.Catalogo.entity.Producto;
import com.example.Catalogo.repository.CategoriaRepository;
import com.example.Catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoriaEjemplo;
    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria();
        categoriaEjemplo.setId(1L);
        categoriaEjemplo.setNombre("Verduras");

        productoEjemplo = new Producto();
        productoEjemplo.setId(1L);
        productoEjemplo.setNombre("Tomate");
        productoEjemplo.setPrecio(500);
        productoEjemplo.setCategoria(categoriaEjemplo);
    }

    @Test
    void listarProductos_debeRetornarListaDeProductos() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoEjemplo));

        List<Producto> resultado = productoRepository.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Tomate", resultado.get(0).getNombre());
    }

    @Test
    void buscarProductoPorId_debeRetornarProductoCuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

        Optional<Producto> resultado = productoRepository.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Tomate", resultado.get().getNombre());
        assertEquals(500, resultado.get().getPrecio());
    }

    @Test
    void buscarProductoPorId_debeRetornarVacioCuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = productoRepository.findById(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void guardarProducto_debeGuardarCorrectamente() {
        when(productoRepository.save(productoEjemplo)).thenReturn(productoEjemplo);

        Producto guardado = productoRepository.save(productoEjemplo);

        assertNotNull(guardado);
        assertEquals("Tomate", guardado.getNombre());
        verify(productoRepository, times(1)).save(productoEjemplo);
    }
    
    
}
