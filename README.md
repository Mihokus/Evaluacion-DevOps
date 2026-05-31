# Microservicio catalogo

Este proyecto consiste en un microservicio de catálogo desarrollado en cursos anteriores con Spring Boot. El cual se utilizó para poder aplicar prácticas de DevOps, enfocándose especialmente en la automatización, buenas prácticas y control de versiones. Se realizan las correciones de EV1.

## Estrategia de trabajo GitFlow
Para este proyecto utilicé el modelo GitFlow.

## Justificacion
Decidí usar este flujo, ya que nos permite organizar de una forma segura el desarrollo que estemos realizando, dividiéndolo en las ramas:
* **main**: Solo contiene el código que ya está probado y listo para producción.
* **develop**: Se utilizó para integrar todos los cambios durante el desarrollo.
* **feature**: Ramas que se utilizan para agregar una funcionalidad específica sin ensuciar el código principal.

## Buenas prácticas y Convenciones
Para mantener el orden en el repositorio, seguí las siguientes reglas:

**Mensajes de commit:** Intenté que los mensajes fueran descriptivos del cambio realizado.
**Estructura del proyecto:** El código sigue el estándar de Maven src/main/java, lo que facilita encontrar los controladores, servicios y entidades del catálogo.
**Gestión de ramas:** Se evitó trabajar directamente sobre main, realizando las integraciones mediante merges desde develop una vez pasadas las pruebas.

## El flujo de trabajo es el siguiente:
* Se activa automáticamente cada que uno suba algo a la rama develop o se abra un Pull Request dentro de la rama main.
* Lo que hace el servidor: descarga el código, configura el entorno a Java 21 y ejecuta ./mvnw test.
* Esto especialmente me ayudó para ir verificando si los cambios que estuve realizando pueden llegar a generarme errores y darme cuenta de esto antes de pasar el código a la rama principal.

**Reflexión Personal:**
Durante esta evaluación he aprendido a automatizar las tareas con GitHub Actions y a seguir reglas de trabajo que nos permiten un mejor desarrollo colaborativo. Gracias a las buenas prácticas vistas en clases, cualquier persona puede entender el código fácilmente y los cambios que se han ido realizando gracias a los commits más descriptivos.

Durante el proceso, cometí algunos errores en la configuración del pipeline, lo que me permitió notar que la herramienta te avisa de forma inmediata si existe algún error sin ella, habría tenido que realizar pruebas manuales una por una hasta hallar el fallo.

Como dato final, considero que GitHub Actions es una herramienta fundamental para lograr un desarrollo continuo y más rápido. Y que nos demuestra cómo es que se trabaja en el mundo real con estas herramientas y siguiendo las buenas prácticas, nos acerca cada vez más a cómo debería verse un profesional.