# Microservicio Catálogo – EP3 DevOps

DOY0101 – Ingeniería DevOps  
Evaluación Parcial 3: Observabilidad y entornos reales

---

## ¿De qué se trata esto?

Este proyecto es la continuación del pipeline que construimos en la EP2. En esta entrega lo que hicimos fue agregarle observabilidad real al microservicio, desplegarlo en Kubernetes y dejar el pipeline configurado para que se detenga solo si algo falla en seguridad o calidad.

El microservicio es un catálogo de productos y categorías hecho en Spring Boot para el sistema Huerto Hogar.

---

## Tecnologías que usamos

- **Spring Boot 3.5 / Java 21** para el microservicio
- **Docker + Docker Compose** para desarrollo local
- **Kubernetes (minikube)** para el despliegue orquestado
- **Prometheus + Grafana** para monitoreo y métricas
- **GitHub Actions** para el pipeline CI/CD
- **JaCoCo** para medir cobertura de pruebas
- **SpotBugs** para análisis estático del código (SAST)
- **SonarCloud** para control de calidad y cumplimiento
- **Snyk** para escaneo de dependencias (SCA)
- **GHCR** (GitHub Container Registry) para almacenar las imágenes Docker

---

## Estructura del proyecto

```
├── .github/workflows/ci-cd.yml   -> pipeline completo
├── k8s/                           -> manifiestos Kubernetes
│   ├── mysql/
│   ├── app/
│   └── monitoring/
├── monitoring/                    -> configuración Prometheus y Grafana
│   ├── prometheus.yml
│   └── grafana/provisioning/
├── src/
│   ├── main/
│   └── test/                      -> tests unitarios nuevos
├── docker-compose.yml
├── Dockerfile
└── sonar-project.properties
```

---

## Pipeline CI/CD

El pipeline tiene 7 etapas en orden. Si cualquiera falla, las siguientes no corren.

```
tests → sast → sonar → build-push → security-scan → deploy-k8s → smoke-test
```

**Etapa 1 – tests:** Corre los tests unitarios y JaCoCo mide la cobertura. Si la cobertura baja del 60% el pipeline falla aquí y no continúa.

**Etapa 2 – sast:** SpotBugs analiza el código en busca de bugs estáticos. Si encuentra algo de severidad HIGH el build falla.

**Etapa 3 – sonar:** Análisis de SonarCloud. Usamos `-Dsonar.qualitygate.wait=true` para que el pipeline espere el resultado del Quality Gate. Si falla, para todo.

**Etapa 4 – build-push:** Construye la imagen Docker y la sube a GHCR con el tag del SHA del commit. Así siempre se puede saber exactamente qué commit generó qué imagen.

**Etapa 5 – security-scan:** Snyk revisa las dependencias Maven. Esta vez sin `continue-on-error`, así que si encuentra vulnerabilidades HIGH o CRITICAL el pipeline se cae.

**Etapa 6 – deploy-k8s:** Levanta un cluster con minikube en el runner de GitHub Actions y despliega todo: MySQL, la aplicación, Prometheus y Grafana con los manifiestos de la carpeta `k8s/`.

**Etapa 7 – smoke-test:** Verifica que `/actuator/health` responde UP, que el endpoint de métricas Prometheus está funcionando y que Prometheus está scrapeando correctamente el microservicio.

---

## Observabilidad (IE1 y IE3)

Para exponer métricas agregamos al `pom.xml` la dependencia `micrometer-registry-prometheus` junto con Spring Boot Actuator. Eso habilita automáticamente el endpoint `/actuator/prometheus` con métricas en formato que Prometheus puede leer.

Las métricas más importantes que recolectamos:

- `jvm_memory_used_bytes` – uso de memoria heap y non-heap
- `process_cpu_usage` – CPU que está usando la JVM
- `http_server_requests_seconds` – latencia, tasa de solicitudes y errores HTTP por endpoint
- `hikaricp_connections_active` – conexiones activas al MySQL
- `jvm_threads_live_threads` – hilos JVM en ejecución

Prometheus scrape cada 10 segundos el endpoint del microservicio. La configuración está en `monitoring/prometheus.yml`.

Grafana se provisiona automáticamente desde `monitoring/grafana/provisioning/`. El dashboard `catalogo-dashboard.json` tiene 10 paneles con los datos más relevantes para tomar decisiones:

- Si la CPU o memoria suben mucho → hay que escalar o revisar fugas de memoria
- Si la latencia P95 supera 500ms → hay que revisar las queries a la BD
- Si aparecen errores 5xx → hay que revisar los logs y posiblemente hacer rollback
- Si las conexiones de HikariCP se saturan → hay que aumentar el pool

---

## Despliegue en Kubernetes (IE2)

Los manifiestos en `k8s/` despliegan todo el stack en el namespace `catalogo`:

- MySQL con un PersistentVolumeClaim de 1Gi para que los datos no se pierdan
- La aplicación con 2 réplicas y estrategia RollingUpdate (sin downtime)
- Prometheus con ServiceAccount que tiene permisos para descubrir pods automáticamente
- Grafana

En el pipeline se usa minikube porque es el entorno que tenemos disponible. La imagen Docker construida en el job anterior se carga directamente con `minikube image load`, así no depende de que el registry sea público.

Los pods del microservicio tienen anotaciones para que Prometheus los descubra automáticamente:
```yaml
prometheus.io/scrape: "true"
prometheus.io/path: "/actuator/prometheus"
prometheus.io/port: "8082"
```

---

## Pruebas unitarias (corrección EP2)

En la EP2 solo teníamos el `contextLoads()` vacío con el `@SpringBootTest` comentado. Lo corregimos escribiendo 16 tests reales:

- **CategoriaServiceTest** (8 tests): cubre listarTodas, crear con éxito, crear cuando ya existe el nombre, actualizar, actualizar cuando no existe, eliminar, eliminar cuando no existe y obtenerEntidadPorId.
- **ProductoServiceTest** (8 tests): cubre listarTodos sin filtros, filtrar por nombre, filtrar por categoría, obtenerPorId, crear con éxito, crear con código duplicado, actualizar y eliminar.
- **CatalogoApplicationTests**: ahora sí usa `@SpringBootTest` con el perfil `test` que levanta una base de datos H2 en memoria.

Para que los tests corran sin MySQL instalado agregamos H2 como dependencia de test y creamos `application-test.properties` con la configuración de H2.

---

## Políticas de cumplimiento (IE5)

### Branch protection en main

En GitHub Settings → Branches configuramos la rama `main` con:
- Requerir al menos 1 revisión de PR
- Requerir que los status checks del pipeline pasen antes de mergear
- No permitir push directo a main
- Descartar revisiones aprobadas cuando llegan nuevos commits

### SonarCloud

Analiza bugs, code smells y vulnerabilidades. El archivo `sonar-project.properties` excluye las clases que son solo boilerplate (entidades, DTOs, config, seguridad) para que el análisis se enfoque en la lógica de negocio.

### SpotBugs

Análisis estático que corre en cada `mvn verify`. Configurado con threshold `High` y `failOnError=true`, así cualquier bug grave rompe el build antes de que llegue a producción.

### Snyk

Revisa las dependencias declaradas en el `pom.xml` buscando CVEs conocidos. Dependabot complementa esto abriendo PRs automáticos cuando hay actualizaciones de dependencias.

---

## Cómo levantar el entorno local

```bash
# 1. Clonar
git clone https://github.com/Mihokus/Evaluacion-DevOps.git
cd Evaluacion-DevOps

# 2. Copiar variables de entorno
cp .env.example .env
# Editar .env con las contraseñas

# 3. Levantar todo 
docker compose up -d --build

# 4. Verificar que todo esté corriendo
docker compose ps
```

Servicios disponibles:
- API: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui/index.html
- Health: http://localhost:8082/actuator/health
- Métricas: http://localhost:8082/actuator/prometheus
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin por defecto)

Para correr los tests localmente:
```bash
./mvnw verify -B
```

---

## Secrets necesarios en GitHub Actions

| Secret | Para qué |
|---|---|
| `SONAR_TOKEN` | Autenticación con SonarCloud |
| `SONAR_PROJECT_KEY` | Clave del proyecto en SonarCloud |
| `SONAR_ORGANIZATION` | Organización en SonarCloud |
| `SNYK_TOKEN` | Autenticación con Snyk |

El `GITHUB_TOKEN` lo provee GitHub automáticamente.

---

## Uso de IA

En este proyecto utilizamos Claude (Anthropic) como apoyo para verificar la sintaxis de los manifiestos Kubernetes y de las configuraciones YAML de Prometheus y Grafana. También lo usamos para revisar la documentación técnica. Las decisiones de arquitectura, el diseño del pipeline y la implementación de los tests fueron realizadas por el equipo.
