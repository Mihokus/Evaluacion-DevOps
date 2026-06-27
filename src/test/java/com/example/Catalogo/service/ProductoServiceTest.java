package com.example.Catalogo.service;

import com.example.Catalogo.dto.ProductoRequest;
import com.example.Catalogo.dto.ProductoResponse;
import com.example.Catalogo.entity.Categoria;
import com.example.Catalogo.entity.Producto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoriaFrutas;

    @BeforeEach
    void setUp() {
        categoriaFrutas = new Categoria(1L, "Frutas", "Frutas frescas");
    }

    private Producto crearProducto(Long id, String codigo, String nombre) {
        Producto p = new Producto();
        p.setId(id);
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setPrecio(500);
        p.setStock(100);
        p.setUnidad("kg");
        p.setDescripcion("Descripción de " + nombre);
        p.setCategoria(categoriaFrutas);
        return p;
    }

    private ProductoRequest crearRequest(String codigo, String nombre, Long catId) {
        ProductoRequest req = new ProductoRequest();
        req.setCodigo(codigo);
        req.setNombre(nombre);
        req.setPrecio(500);
        req.setStock(100);
        req.setUnidad("kg");
        req.setCategoriaId(catId);
        return req;
    }

    @Test
    void listarTodos_sinFiltros_retornaTodosLosProductos() {
        List<Producto> productos = Arrays.asList(
                crearProducto(1L, "FRU001", "Manzana"),
                crearProducto(2L, "FRU002", "Pera")
        );
        when(productoRepository.findAll()).thenReturn(productos);

        List<ProductoResponse> resultado = productoService.listarTodos(null, null);

        assertEquals(2, resultado.size());
        assertEquals("Manzana", resultado.get(0).getNombre());
        assertEquals("Pera", resultado.get(1).getNombre());
    }

    @Test
    void listarTodos_filtrandoPorNombre_buscaPorNombre() {
        List<Producto> productos = List.of(crearProducto(1L, "FRU001", "Manzana"));
        when(productoRepository.findByNombreContainingIgnoreCase("manz")).thenReturn(productos);

        List<ProductoResponse> resultado = productoService.listarTodos("manz", null);

        assertEquals(1, resultado.size());
        assertEquals("FRU001", resultado.get(0).getCodigo());
        verify(productoRepository, times(1)).findByNombreContainingIgnoreCase("manz");
        verify(productoRepository, never()).findAll();
    }

    @Test
    void listarTodos_filtrandoPorCategoria_buscaPorCategoria() {
        List<Producto> productos = List.of(crearProducto(1L, "FRU001", "Manzana"));
        when(categoriaService.obtenerEntidadPorId(1L)).thenReturn(categoriaFrutas);
        when(productoRepository.findByCategoria(categoriaFrutas)).thenReturn(productos);

        List<ProductoResponse> resultado = productoService.listarTodos(null, 1L);

        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findByCategoria(categoriaFrutas);
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaProducto() {
        Producto producto = crearProducto(1L, "FRU001", "Manzana");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponse resultado = productoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("FRU001", resultado.getCodigo());
        assertEquals(500, resultado.getPrecio());
        assertEquals("Frutas", resultado.getCategoriaNombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void crear_cuandoCodigoNoDuplicado_creaCorrectamente() {
        ProductoRequest request = crearRequest("FRU001", "Manzana", 1L);
        Producto guardado = crearProducto(1L, "FRU001", "Manzana");

        when(productoRepository.existsByCodigo("FRU001")).thenReturn(false);
        when(categoriaService.obtenerEntidadPorId(1L)).thenReturn(categoriaFrutas);
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponse resultado = productoService.crear(request);

        assertNotNull(resultado);
        assertEquals("Manzana", resultado.getNombre());
        assertEquals(1L, resultado.getCategoriaId());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void crear_cuandoCodigoYaExiste_lanzaExcepcion() {
        ProductoRequest request = crearRequest("FRU001", "Manzana", 1L);
        when(productoRepository.existsByCodigo("FRU001")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.crear(request));
        assertTrue(ex.getMessage().contains("Ya existe producto con código"));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoProductoExiste_eliminaCorrectamente() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        assertDoesNotThrow(() -> productoService.eliminar(1L));
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoProductoNoExiste_lanzaExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.eliminar(99L));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(productoRepository, never()).deleteById(any());
    }
}
