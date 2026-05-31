# Microservicio Catálogo

Este proyecto corresponde a un microservicio de catálogo de productos desarrollado con Spring Boot. Su función principal es administrar productos y categorías, incorporando autenticación mediante JWT para proteger los distintos endpoints. El sistema fue creado en una asignatura anterior y, durante este semestre, se utilizó como base para aplicar distintas herramientas y prácticas relacionadas con DevOps.

# Estrategia de Ramificación

Para organizar el desarrollo del proyecto se decidió utilizar GitFlow, ya que permite separar claramente el código que se encuentra en desarrollo del que está listo para producción. De esta forma es más fácil trabajar en nuevas funcionalidades sin afectar la versión estable de la aplicación.

Las ramas utilizadas fueron las siguientes:

* **main:** contiene la versión estable del proyecto y representa el código listo para producción.
* **develop:** se utiliza para integrar los cambios desarrollados antes de ser enviados a main.
* **feature/***: destinada al desarrollo de nuevas funcionalidades o mejoras específicas.

La principal razón para utilizar GitFlow fue la necesidad de mantener un orden claro durante el desarrollo. Existen estrategias más simples, como GitHub Flow o Trunk-Based Development, pero GitFlow entrega una mejor separación entre el trabajo en progreso y las versiones estables, algo que resulta útil cuando se trabaja en varias funcionalidades al mismo tiempo.


# Buenas Prácticas de Desarrollo

## Convención para nombres de ramas

Se siguió una estructura simple para identificar rápidamente el propósito de cada rama:

* `feature/agregar-validaciones`
* `feature/gestion-categorias`
* `hotfix/correccion-jwt`
* `release/1.0.0`

Como regla general, los nombres se escriben en minúsculas y utilizando guiones para separar palabras.

## Convención de commits

Para mantener un historial ordenado se utilizó la convención Conventional Commits. Algunos ejemplos son:

* `feat: agregar búsqueda de productos por nombre`
* `fix: corregir validación de stock`
* `docs: actualizar documentación del proyecto`
* `test: agregar pruebas unitarias de ProductoService`

Esto permite identificar rápidamente el tipo de cambio realizado y facilita el seguimiento de la evolución del proyecto.

## Proceso de revisión

Antes de integrar cambios a las ramas principales se siguió el siguiente flujo:

1. Crear una rama a partir de develop.
2. Realizar los cambios correspondientes.
3. Subir los cambios al repositorio.
4. Crear un Pull Request.
5. Revisar el código antes de aprobar la integración.
6. Eliminar la rama una vez completado el merge.

Esta práctica ayuda a detectar errores antes de que lleguen a las ramas principales.
# Implementación de CI/CD
La Integración Continua (CI) permite ejecutar pruebas automáticamente cada vez que se realizan cambios en el repositorio. Por otro lado, la Entrega Continua (CD) automatiza parte del proceso de despliegue.

La incorporación de CI/CD aporta varias ventajas:

* Detectar errores de forma temprana.
* Reducir tareas manuales repetitivas.
* Asegurar que el código pase por validaciones antes de ser desplegado.
* Mantener una mejor calidad del software.

## Pipeline inicial

Durante la primera etapa se configuró un workflow que se ejecuta cuando se realizan cambios en la rama develop o cuando se crea un Pull Request hacia main.

Las tareas realizadas fueron:

1. Descargar el código desde GitHub.
2. Configurar Java 21.
3. Ejecutar las pruebas unitarias mediante Maven.

## Pipeline completo

Posteriormente se incorporaron nuevas etapas al proceso:

### Ejecución de pruebas

Se ejecutan los tests unitarios para validar el correcto funcionamiento de la aplicación.

### Construcción de imagen Docker

Se genera una imagen del microservicio utilizando el Dockerfile definido en el proyecto.

### Análisis de seguridad

Se utilizó Snyk para revisar dependencias vulnerables. En caso de detectar problemas críticos, la ejecución se detiene para evitar desplegar una versión insegura.

### Despliegue automatizado

Finalmente, si todas las validaciones son exitosas, se realiza el despliegue utilizando Docker Compose junto con una base de datos MySQL.

## Trazabilidad

GitHub Actions permite visualizar cada ejecución realizada, incluyendo el commit asociado, el resultado de cada etapa y los posibles errores encontrados. Esto facilita la identificación y corrección de problemas.
# Contenedores y Orquestación

## Docker

Para contenerizar la aplicación se utilizó una imagen base de Eclipse Temurin con Java 21.

El Dockerfile está dividido en dos etapas:

* Una etapa de compilación, donde se genera el archivo JAR.
* Una etapa de ejecución, donde solo se copia el artefacto necesario para reducir el tamaño final de la imagen.

## Docker Compose

Docker Compose se utilizó para administrar tanto el microservicio como la base de datos.

Los servicios definidos son:

* **catalogo-app:** aplicación Spring Boot.
* **mysql-db:** base de datos MySQL.

Gracias a Docker Compose es posible iniciar ambos servicios con un solo comando y asegurar la comunicación entre ellos.

Para iniciar el entorno:

```bash
docker compose up --build
```
Para detenerlo:

```bash
docker compose down
```
# Reflexión Personal

A través de este proyecto pude comprender mejor cómo se relacionan distintas herramientas utilizadas en el desarrollo moderno de software. Antes conocía conceptos como GitFlow, Docker o GitHub Actions de manera individual, pero al implementarlos en un mismo proyecto fue posible entender cómo trabajan en conjunto.

Uno de los aspectos más complejos fue lograr que las pruebas funcionaran correctamente dentro de GitHub Actions, ya que el entorno de ejecución es distinto al de desarrollo local. Esto permitió comprender la importancia de las pruebas unitarias y del uso de herramientas como Mockito para evitar depender directamente de una base de datos real.

También resultó interesante utilizar herramientas de análisis de seguridad como Snyk, ya que muestran vulnerabilidades que normalmente pasan desapercibidas durante el desarrollo.

En general, esta experiencia permitió acercarse a un flujo de trabajo similar al utilizado en entornos profesionales, entendiendo mejor cómo se gestionan los cambios, las validaciones automáticas y los despliegues dentro de un proyecto de software.
