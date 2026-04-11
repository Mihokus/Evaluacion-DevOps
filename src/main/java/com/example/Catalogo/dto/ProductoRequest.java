package com.example.Catalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede superar 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Integer precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Size(max = 50, message = "La unidad no puede superar 50 caracteres")
    private String unidad;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @Size(max = 500, message = "La URL de la imagen no puede superar 500 caracteres")
    private String imagenUrl;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
}
