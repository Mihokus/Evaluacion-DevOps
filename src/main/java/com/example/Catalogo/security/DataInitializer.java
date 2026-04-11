package com.example.Catalogo.security;

import com.example.Catalogo.entity.Categoria;
import com.example.Catalogo.entity.Producto;
import com.example.Catalogo.repository.CategoriaRepository;
import com.example.Catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {


        if (categoriaRepository.count() == 0) {
            System.out.println("Inicializando categorías...");
            categoriaRepository.saveAll(Arrays.asList(
                    new Categoria(null, "Frutas Frescas", "Frutas de temporada recién cosechadas"),
                    new Categoria(null, "Verduras Orgánicas", "Verduras cultivadas sin pesticidas"),
                    new Categoria(null, "Productos Orgánicos", "Variedad de productos 100% orgánicos"),
                    new Categoria(null, "Productos Lácteos", "Leche, queso y derivados naturales")
            ));
            System.out.println("Categorías creadas.");
        }
        if (productoRepository.count() == 0) {
            System.out.println("Inicializando productos...");
            Categoria frutas = categoriaRepository.findByNombre("Frutas Frescas").orElse(null);
            Categoria verduras = categoriaRepository.findByNombre("Verduras Orgánicas").orElse(null);
            Categoria lacteos = categoriaRepository.findByNombre("Productos Lácteos").orElse(null);


            if (frutas != null && verduras != null && lacteos != null) {
                List<Producto> productos = Arrays.asList(
                        new Producto(null, "FR001", "Manzana Fuji", 1200, 150, "por kilo",
                                "Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule.",
                                "https://img.freepik.com/fotos-premium/primer-plano-manzanas-bandeja-sobre-mesa_1048944-4636814.jpg",
                                frutas),

                        new Producto(null, "FR002", "Naranjas Valencia", 1000, 200, "por kilo",
                                "Jugosas y ricas en vitamina C, estas naranjas Valencia son ideales para zumos.",
                                "https://img.freepik.com/foto-gratis/vista-horizontal-naranjas-frescas-enteras-cortadas-mitad-sobre-fondo-gris_140725-140758.jpg",
                                frutas),

                        new Producto(null, "PL001", "Leche Entera", 1200, 100, "por litro",
                                "Leche entera fresca proveniente de granjas locales comprometidas.",
                                "https://img.freepik.com/foto-gratis/botellas-leche-fresca-galletas-americanas_23-2148239836.jpg",
                                lacteos),

                        new Producto(null, "VR002", "Espinacas Frescas", 700, 80, "por bolsa de 500g",
                                "Espinacas frescas y nutritivas, perfectas para ensaladas y batidos verdes.",
                                "https://img.freepik.com/fotos-premium/recipiente-madera-espinacas-servilleta-mesa-madera_185193-20003.jpg",
                                verduras),

                        new Producto(null, "VR001", "Zanahorias Orgánicas", 900, 100, "por kilo",
                                "Zanahorias crujientes cultivadas sin pesticidas en la Región de O'Higgins.",
                                "https://img.freepik.com/fotos-premium/primer-plano-zanahorias-mesa-madera_1048944-28938675.jpg",
                                verduras)
                );

                productoRepository.saveAll(productos);
                System.out.println("Productos creados exitosamente.");
            } else {
                System.out.println("ADVERTENCIA: No se pudieron crear los productos porque no se encontraron las categorías.");
            }
        }
    }
}