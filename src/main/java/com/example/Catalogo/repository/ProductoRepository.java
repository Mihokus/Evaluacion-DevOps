package com.example.Catalogo.repository;
import com.example.Catalogo.entity.Categoria;
import com.example.Catalogo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
