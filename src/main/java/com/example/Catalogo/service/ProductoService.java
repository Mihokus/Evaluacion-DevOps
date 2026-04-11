package com.example.Catalogo.service;
import com.example.Catalogo.dto.ProductoRequest;
import com.example.Catalogo.dto.ProductoResponse;
import com.example.Catalogo.entity.Categoria;
import com.example.Catalogo.entity.Producto;
import com.example.Catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public List<ProductoResponse> listarTodos(String nombre, Long categoriaId) {
        List<Producto> productos;

        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (categoriaId != null) {
            Categoria categoria = categoriaService.obtenerEntidadPorId(categoriaId);
            productos = productoRepository.findByCategoria(categoria);
        } else {
            productos = productoRepository.findAll();
        }

        return productos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return toResponse(producto);
    }

    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe producto con código: " + request.getCodigo());
        }

        Categoria categoria = categoriaService.obtenerEntidadPorId(request.getCategoriaId());

        Producto producto = new Producto();
        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setUnidad(request.getUnidad());
        producto.setDescripcion(request.getDescripcion());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setCategoria(categoria);

        productoRepository.save(producto);

        return toResponse(producto);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!producto.getCodigo().equals(request.getCodigo())
                && productoRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe producto con código: " + request.getCodigo());
        }

        Categoria categoria = categoriaService.obtenerEntidadPorId(request.getCategoriaId());

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setUnidad(request.getUnidad());
        producto.setDescripcion(request.getDescripcion());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setCategoria(categoria);

        productoRepository.save(producto);

        return toResponse(producto);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    private ProductoResponse toResponse(Producto producto) {
        ProductoResponse res = new ProductoResponse();
        res.setId(producto.getId());
        res.setCodigo(producto.getCodigo());
        res.setNombre(producto.getNombre());
        res.setPrecio(producto.getPrecio());
        res.setStock(producto.getStock());
        res.setUnidad(producto.getUnidad());
        res.setDescripcion(producto.getDescripcion());
        res.setImagenUrl(producto.getImagenUrl());
        res.setCategoriaId(producto.getCategoria().getId());
        res.setCategoriaNombre(producto.getCategoria().getNombre());
        return res;
    }
}
