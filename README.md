# Automatización de Quality Gates y Pruebas Continuas en una API REST de Java mediante Pipelines de Jenkins y Docker, JaCoCo + Checkstyle

## 📋 Descripción

API REST de gestión de productos desarrollada en **Spring Boot 3.2** con **Java 21**, implementando un pipeline de CI/CD en **Jenkins** con **Quality Gates** automatizados mediante **Docker**.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────┐
│              Jenkins Pipeline (CI/CD)            │
│  Checkout → Build → Tests → Coverage → Deploy   │
└──────────────────────┬──────────────────────────┘
                       │
           ┌───────────┴───────────┐
           │    Docker Compose      │
           │  ┌──────┐  ┌───────┐  │
           │  │ API  │  │ MySQL │  │
           │  │:8081 │  │:3307  │  │
           │  └──────┘  └───────┘  │
           └───────────────────────┘
```

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|---|---|---|
| Java JDK | 21 | Lenguaje de programación |
| Spring Boot | 3.2.5 | Framework web |
| Maven | 3.9.x | Gestión de dependencias |
| MySQL | 8.4 | Base de datos |
| JUnit 5 | 5.x | Pruebas unitarias |
| Mockito | 5.x | Mocking para tests |
| JaCoCo | 0.8.12 | Cobertura de código |
| Checkstyle | 3.3.1 | Análisis estático |
| Docker | 28.x | Contenedorización |
| Jenkins | LTS | CI/CD Pipeline |

## 🚀 Ejecutar Localmente

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar con Maven
mvn spring-boot:run

# Ejecutar con Docker Compose
docker-compose up -d
```

## 📡 API Endpoints

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/products` | Listar todos los productos |
| GET | `/api/v1/products/{id}` | Obtener producto por ID |
| POST | `/api/v1/products` | Crear nuevo producto |
| PUT | `/api/v1/products/{id}` | Actualizar producto |
| DELETE | `/api/v1/products/{id}` | Eliminar producto |
| GET | `/api/v1/products/category/{cat}` | Filtrar por categoría |
| GET | `/api/v1/products/search?name=X` | Buscar por nombre |

## 🚦 Quality Gates

| Gate | Herramienta | Umbral |
|---|---|---|
| Cobertura de código | JaCoCo | ≥ 80% líneas |
| Cobertura de ramas | JaCoCo | ≥ 70% branches |
| Análisis estático | Checkstyle | 0 errores |
| Tests unitarios | JUnit 5 | 100% pasando |
| Tests integración | JUnit 5 | 100% pasando |

## 👨‍💻 Autor

José Díaz López
