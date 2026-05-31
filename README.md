# Microservicio Catálogo

Microservicio de catálogo de productos desarrollado con Spring Boot en un ramo anterior. Se utiliza como base para aplicar prácticas DevOps a lo largo del semestre, incorporando control de versiones, automatización con CI/CD, contenedores y análisis de seguridad.

---

## Estrategia de ramificación — GitFlow

Se eligió GitFlow como modelo de trabajo porque permite separar claramente lo que está en desarrollo de lo que ya está listo para producción. A diferencia de GitHub Flow, que es más simple pero no maneja bien los errores urgentes en producción, GitFlow tiene ramas específicas para cada situación.

### Ramas utilizadas

| Rama | Para qué se usa |
|---|---|
| `main` | Solo código probado y listo para producción |
| `develop` | Integración de todos los cambios antes de pasar a main |
| `feature/<nombre>` | Una rama por cada funcionalidad nueva, se integra a develop mediante PR |
| `hotfix/<nombre>` | Correcciones urgentes que salen desde main y se mergean a main y develop |

La rama hotfix es importante porque permite corregir un error en producción sin tener que esperar a que termine todo el desarrollo que está en curso en develop.

---

## Buenas prácticas del repositorio

### Mensajes de commit

Se usa la convención Conventional Commits para que el historial sea fácil de leer:

| Prefijo | Cuándo se usa |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `hotfix:` | Corrección urgente en producción |
| `docs:` | Cambios en documentación |
| `chore:` | Mantenimiento, dependencias, configuración |
| `test:` | Tests nuevos o modificados |

Ejemplos:
```
feat: agregar Dockerfile para contenerizar el microservicio
fix: corregir URL de conexion MySQL en docker-compose
chore: actualizar Spring Boot a 3.5.0 para corregir vulnerabilidades
```

### Flujo de trabajo con Pull Requests

Nunca se hace push directo a main ni a develop. Todo cambio entra mediante un Pull Request, lo que permite revisar el código antes de integrarlo. Una vez mergeado el PR, se elimina la rama para mantener el repo ordenado.

### Estructura del proyecto

Sigue el estándar Maven:
```
src/
  main/java/com/example/Catalogo/
    controller/    endpoints REST
    service/       lógica de negocio
    repository/    acceso a base de datos
    entity/        modelos JPA
    dto/           objetos de transferencia
    security/      JWT y Spring Security
  test/            tests unitarios
```

---

## CI/CD con GitHub Actions

### Por qué usamos CI/CD

Cada vez que se sube código, el pipeline ejecuta automáticamente los pasos de validación. Esto evita tener que correr las pruebas a mano y garantiza que lo que llega a main siempre pasó por un proceso de verificación. Si algo falla, GitHub lo notifica de inmediato en lugar de enterarse después.

### Pipeline básico — ci.yml (EP1)

Se activa con push a `develop` y pull request a `main`. Configura Java 21 y ejecuta `./mvnw test`.

### Pipeline completo — ci-cd.yml (EP2)

Se activa con las mismas condiciones y tiene 4 etapas que se ejecutan en orden:

**1. Tests** — Corre los tests unitarios con Maven. Si fallan, las etapas siguientes no se ejecutan.

**2. Build Docker** — Construye la imagen del microservicio usando el Dockerfile. Verifica que el contenedor compila correctamente.

**3. Escaneo de seguridad con Snyk** — Analiza las dependencias del proyecto buscando vulnerabilidades conocidas. En esta evaluación se encontraron vulnerabilidades en Tomcat y Spring Boot que requieren versiones aún no disponibles como release estable, o un salto a versión mayor (4.0.0) que está fuera del alcance del proyecto. El paso está configurado con `continue-on-error: true` para que el pipeline continúe, pero el reporte queda visible en Actions como evidencia del análisis.

**4. Deploy con Docker Compose** — Levanta el microservicio junto a MySQL en un entorno simulado y verifica que el servicio responde.

### Trazabilidad

Cada ejecución queda registrada en la pestaña Actions de GitHub con el commit que la disparó, las etapas que pasaron o fallaron y el log completo de cada paso.

---

## Contenedores y orquestación

### Dockerfile

Usa una imagen base de Eclipse Temurin con Java 21. Tiene dos etapas: una para compilar con Maven y otra solo con el JRE para ejecutar el JAR, lo que reduce el tamaño final de la imagen.

### Docker Compose

Orquesta dos servicios: el microservicio (`catalogo-app`) y la base de datos (`mysql-db`). El microservicio espera a que MySQL esté saludable antes de arrancar, usando el healthcheck configurado en el servicio de base de datos.

Para levantar el proyecto localmente:
```bash
docker compose up --build
```

Para detenerlo:
```bash
docker compose down
```

---

## Reflexión personal

Al principio no entendía muy bien para qué servía tener tantas ramas si al final todo terminaba en main igual. Pero durante el desarrollo me fui dando cuenta de que el punto no es solo llegar a main, sino poder trabajar en varias cosas al mismo tiempo sin que se mezclen los cambios. Cuando tuve que hacer un hotfix por el error de conexión de MySQL en Docker, entendí en la práctica por qué existe esa rama separada.

Lo que más me costó fue configurar el pipeline de Snyk. Tuve varios errores antes de que funcionara, primero por un problema con JAVA_HOME dentro del contenedor que usa la acción, y después porque Snyk detectó vulnerabilidades reales en las dependencias. Eso me hizo investigar qué significaba cada vulnerabilidad y por qué no todas tienen fix disponible inmediatamente.

En general esta evaluación me mostró que DevOps no es solo automatizar, sino también entender qué está pasando cuando algo falla y tomar decisiones informadas sobre cómo manejarlo.