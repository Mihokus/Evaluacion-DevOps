package com.example.Catalogo.controller;

import com.example.Catalogo.dto.ProductoRequest;
import com.example.Catalogo.dto.ProductoResponse;
import com.example.Catalogo.service.ProductoService;
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
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @Test
    void listar_sinFiltros_retornaLista() {
        ProductoResponse p1 = new ProductoResponse();
        p1.setNombre("Manzana");
        ProductoResponse p2 = new ProductoResponse();
        p2.setNombre("Pera");

        when(productoService.listarTodos(null, null)).thenReturn(Arrays.asList(p1, p2));

        List<ProductoResponse> resultado = productoController.listar(null, null);

        assertEquals(2, resultado.size());
        assertEquals("Manzana", resultado.get(0).getNombre());
        verify(productoService, times(1)).listarTodos(null, null);
    }

    @Test
    void obtener_retornaProducto() {
        ProductoResponse response = new ProductoResponse();
        response.setCodigo("FRU001");

        when(productoService.obtenerPorId(1L)).thenReturn(response);

        ProductoResponse resultado = productoController.obtener(1L);

        assertNotNull(resultado);
        assertEquals("FRU001", resultado.getCodigo());
        verify(productoService, times(1)).obtenerPorId(1L);
    }

    @Test
    void crear_retornaProductoCreado() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Manzana");

        ProductoResponse response = new ProductoResponse();
        response.setNombre("Manzana");

        when(productoService.crear(any(ProductoRequest.class))).thenReturn(response);

        ProductoResponse resultado = productoController.crear(request);

        assertNotNull(resultado);
        assertEquals("Manzana", resultado.getNombre());
        verify(productoService, times(1)).crear(request);
    }

    @Test
    void actualizar_retornaProductoActualizado() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Manzana Roja");

        ProductoResponse response = new ProductoResponse();
        response.setNombre("Manzana Roja");

        when(productoService.actualizar(eq(1L), any(ProductoRequest.class))).thenReturn(response);

        ProductoResponse resultado = productoController.actualizar(1L, request);

        assertNotNull(resultado);
        assertEquals("Manzana Roja", resultado.getNombre());
        verify(productoService, times(1)).actualizar(1L, request);
    }

    @Test
    void eliminar_llamaAlServicio() {
        doNothing().when(productoService).eliminar(1L);

        productoController.eliminar(1L);

        verify(productoService, times(1)).eliminar(1L);
    }
}