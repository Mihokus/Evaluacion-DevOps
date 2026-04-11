package com.example.Catalogo.dto;

import lombok.Data;

@Data
public class ProductoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer precio;
    private Integer stock;
    private String unidad;
    private String descripcion;
    private String imagenUrl;

    private Long categoriaId;
    private String categoriaNombre;
}
