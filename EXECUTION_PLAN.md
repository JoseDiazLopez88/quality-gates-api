# 🚦 Plan de Ejecución — Quality Gates API
### CI/CD con Jenkins + Docker + JaCoCo + Checkstyle

> **Proyecto:** `quality-gates-api`  
> **Stack:** Java 21 · Spring Boot 3.2.5 · Maven · Docker · Jenkins · MySQL 8.4  
> **Propósito:** Demostrar cómo los Quality Gates garantizan que solo el código de calidad llega a producción.

---

## 📋 Tabla de Contenidos

1. [¿Qué son los Quality Gates?](#1--qué-son-los-quality-gates)
2. [Arquitectura del Sistema](#2--arquitectura-del-sistema)
3. [Estructura del Proyecto](#3--estructura-del-proyecto)
4. [PASO 1 — Levantar los contenedores Docker](#4-paso-1--levantar-los-contenedores-docker)
5. [PASO 2 — Configurar Jenkins](#5-paso-2--configurar-jenkins)
6. [PASO 3 — Crear el Pipeline en Jenkins](#6-paso-3--crear-el-pipeline-en-jenkins)
7. [PASO 4 — Ejecutar el Pipeline](#7-paso-4--ejecutar-el-pipeline)
8. [PASO 5 — Quality Gate: Pruebas Unitarias](#8-paso-5--quality-gate-pruebas-unitarias)
9. [PASO 6 — Quality Gate: Cobertura JaCoCo](#9-paso-6--quality-gate-cobertura-jacoco)
10. [PASO 7 — Quality Gate: Análisis Estático Checkstyle](#10-paso-7--quality-gate-análisis-estático-checkstyle)
11. [PASO 8 — Docker Build & Deploy](#11-paso-8--docker-build--deploy)
12. [PASO 9 — Verificar la API desplegada](#12-paso-9--verificar-la-api-desplegada)
13. [Resultado Final — Pipeline Exitoso](#13-resultado-final--pipeline-exitoso)
14. [Escenario de Fallo — Quality Gate rechaza el código](#14-escenario-de-fallo--quality-gate-rechaza-el-código)
15. [Resumen para la Presentación](#15-resumen-para-la-presentación)

---

## 1. 🚦 ¿Qué son los Quality Gates?

Un **Quality Gate** es una barrera de calidad automática dentro de un pipeline CI/CD.
Su función es **bloquear el avance del código** si no cumple estándares mínimos predefinidos.

### ¿Por qué importan?

| Sin Quality Gates | Con Quality Gates |
|---|---|
| Código malo llega a producción | Solo código que pasa los estándares avanza |
| Los bugs se descubren tarde | Los bugs se detectan en el momento del commit |
| Revisión manual propensa a errores | Validación automática, objetiva y reproducible |
| Deuda técnica acumulada | Calidad mantenida de forma continua |

### Los 3 Quality Gates de este proyecto

```
┌─────────────────────────────────────────────────────────────┐
│                    QUALITY GATES                            │
│                                                             │
│  ✅ QG-1: Pruebas Unitarias + Integración → 100% PASS      │
│  ✅ QG-2: Cobertura de Código (JaCoCo) → mínimo 80%        │
│  ✅ QG-3: Análisis Estático (Checkstyle) → 0 errores       │
│                                                             │
│  Si CUALQUIERA falla → ❌ Pipeline se DETIENE               │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                    DOCKER NETWORK: quality-gates-network        │
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────────┐  │
│  │   qg_mysql   │    │  qg_jenkins  │    │   qg_api         │  │
│  │  MySQL 8.4   │◄───│  Jenkins LTS │───►│  Spring Boot     │  │
│  │  Port: 3307  │    │  Port: 8080  │    │  Port: 8081      │  │
│  └──────────────┘    └──────────────┘    └──────────────────┘  │
│                             │                                   │
│                             │ (builds y despliega)              │
│                             ▼                                   │
│                    qg_api_deployed (Port: 8082)                 │
└─────────────────────────────────────────────────────────────────┘
```

**Flujo de datos:**
1. El desarrollador hace `git push` al repositorio
2. Jenkins detecta el cambio (webhook o trigger manual)
3. Jenkins ejecuta el pipeline con todas las etapas
4. Si los Quality Gates pasan → se construye la imagen Docker
5. La imagen se despliega como contenedor en el puerto 8082

---

## 3. 📁 Estructura del Proyecto

```
quality-gates-api/
├── 📄 Jenkinsfile              ← Pipeline declarativo (8 etapas)
├── 📄 Dockerfile               ← Multi-stage build (Maven → JRE Alpine)
├── 📄 docker-compose.yml       ← Orquestación: MySQL + Jenkins + API
├── 📄 pom.xml                  ← Dependencias + plugins JaCoCo/Checkstyle
├── 📄 checkstyle.xml           ← Reglas de estilo (basadas en Google Style)
└── src/
    ├── main/java/com/qualitygates/api/
    │   ├── 📄 QualityGatesApiApplication.java
    │   ├── controller/
    │   │   └── 📄 ProductController.java   ← 6 endpoints REST
    │   ├── model/
    │   │   └── 📄 Product.java             ← Entidad JPA
    │   ├── service/
    │   │   └── 📄 ProductService.java      ← Lógica de negocio
    │   ├── repository/
    │   │   └── 📄 ProductRepository.java   ← Acceso a datos
    │   └── exception/
    │       └── 📄 ResourceNotFoundException.java
    └── test/java/com/qualitygates/api/
        ├── controller/                      ← Tests del controller
        ├── service/                         ← Tests del servicio
        └── integration/                     ← Tests de integración
```

---

## 4. PASO 1 — Levantar los contenedores Docker

### Prerequisitos
- Docker Desktop instalado y corriendo
- Git con el repositorio clonado localmente
- Puerto 3307, 8080, 8081 disponibles

### Comando

Abre una terminal en la carpeta del proyecto y ejecuta:

```bash
# Ir al directorio del proyecto
cd C:\Users\ASUS I5\Documents\quality-gates-api

# Levantar todos los servicios en segundo plano
docker compose up -d

# Ver el status de los contenedores
docker compose ps
```

### Salida esperada en terminal

```
[+] Running 4/4
 ✔ Network quality-gates-network  Created
 ✔ Container qg_mysql             Started
 ✔ Container qg_jenkins           Started
 ✔ Container qg_api               Started
```

### Verificar que los contenedores están corriendo

```bash
docker compose ps
```

```
NAME          IMAGE                     STATUS              PORTS
qg_mysql      mysql:8.4                 Up (healthy)        0.0.0.0:3307->3306/tcp
qg_jenkins    jenkins/jenkins:lts-jdk21 Up                  0.0.0.0:8080->8080/tcp
qg_api        quality-gates-api-api     Up                  0.0.0.0:8081->8081/tcp
```

---

### 📸 IMAGEN 1 — Docker Compose levantado

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Abre Docker Desktop en tu máquina
> 2. Ejecuta `docker compose up -d` en la terminal
> 3. Espera a que todos los contenedores aparezcan como "Running"
> 4. Toma una captura de **Docker Desktop** mostrando los 3 contenedores activos (`qg_mysql`, `qg_jenkins`, `qg_api`)
> 5. También puedes capturar la terminal con `docker compose ps` mostrando todos en estado `Up`
>
> **Lo que debe verse:** Los tres contenedores en color verde/running en Docker Desktop

```
[ INSERTAR AQUÍ: Captura de Docker Desktop con los 3 contenedores en estado Running ]
```

---

## 5. PASO 2 — Configurar Jenkins

### Acceder a Jenkins

Abre tu navegador y ve a:

```
http://localhost:8080
```

### Obtener la contraseña inicial de Jenkins

```bash
docker exec qg_jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Copia el hash que te devuelve y pégalo en la pantalla de setup de Jenkins.

### Configuración inicial Jenkins

1. **Instalar plugins sugeridos** → Haz clic en "Install suggested plugins"
2. **Crear usuario admin** → Completa el formulario con tu usuario/contraseña
3. **Configurar Maven** en Jenkins:
   - Ir a: `Manage Jenkins` → `Tools`
   - Sección **Maven installations** → `Add Maven`
   - **Name:** `Maven-3.9`
   - **Version:** 3.9.6 (o la más reciente)
   - ✅ Marcar "Install automatically"
   - Guardar

> ⚠️ **IMPORTANTE:** El nombre del Maven en Jenkins **DEBE** ser exactamente `Maven-3.9` porque el `Jenkinsfile` lo referencia así:
> ```groovy
> tools {
>     maven 'Maven-3.9'   // ← debe coincidir exactamente
> }
> ```

---

### 📸 IMAGEN 2 — Jenkins en primer login

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Abre `http://localhost:8080` en tu navegador
> 2. Si es la primera vez, verás la pantalla "Unlock Jenkins"
> 3. Si ya configuraste Jenkins, verás el dashboard principal
> 4. Toma una captura del **dashboard de Jenkins** (`http://localhost:8080`) mostrando el menú de navegación a la izquierda y el botón "New Item" o "Create a job"
>
> **Lo que debe verse:** El panel de control de Jenkins con opciones de menú visibles

```
[ INSERTAR AQUÍ: Captura del Dashboard de Jenkins en http://localhost:8080 ]
```

---

### 📸 IMAGEN 3 — Configuración de Maven en Jenkins Tools

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Ve a `Manage Jenkins` → `Tools`
> 2. Baja hasta la sección "Maven installations"
> 3. Muestra la configuración con `Maven-3.9` como nombre
> 4. Toma la captura mostrando el campo "Name" con el valor `Maven-3.9`
>
> **Lo que debe verse:** La sección de Maven con el nombre exacto `Maven-3.9`

```
[ INSERTAR AQUÍ: Captura de la configuración de Maven en Manage Jenkins → Tools ]
```

---

## 6. PASO 3 — Crear el Pipeline en Jenkins

### Crear el Job

1. En el dashboard de Jenkins → clic en **"New Item"**
2. Nombre del job: `quality-gates-pipeline`
3. Selecciona: **"Pipeline"**
4. Clic en **OK**

### Configurar el Pipeline

En la sección **Pipeline**:
- **Definition:** `Pipeline script from SCM`
- **SCM:** `Git`
- **Repository URL:** La URL de tu repositorio (GitHub o ruta local)
- **Branch Specifier:** `*/main` (o `*/master`)
- **Script Path:** `Jenkinsfile`

**Alternativamente**, puedes usar **"Pipeline script"** y pegar el contenido del `Jenkinsfile` directamente (útil para presentaciones sin acceso a internet).

### Guardar

Clic en **"Save"**.

---

### 📸 IMAGEN 4 — Configuración del Pipeline en Jenkins

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Ve al job `quality-gates-pipeline` → `Configure`
> 2. Baja hasta la sección "Pipeline"
> 3. Muestra la configuración completa: `Pipeline script from SCM`, la URL del repositorio y el branch
> 4. Toma la captura de esa sección completa
>
> **Lo que debe verse:** La configuración del pipeline con la URL del repositorio Git y `Jenkinsfile` como script path

```
[ INSERTAR AQUÍ: Captura de la sección Pipeline en la configuración del Job ]
```

---

## 7. PASO 4 — Ejecutar el Pipeline

### Trigger Manual

En el job `quality-gates-pipeline`:
1. Clic en **"Build Now"**
2. Verás un nuevo build aparecer en "Build History" (ej: `#1`)
3. Haz clic en el número del build → `Console Output` para ver los logs en tiempo real

### Las 8 Etapas del Pipeline

```
Pipeline: quality-gates-api
│
├── 1. ✅ Checkout         → Descarga el código fuente desde Git
├── 2. ✅ Build            → mvn clean compile -B
├── 3. ✅ Unit Tests       → mvn test -B (genera surefire-reports)
├── 4. ✅ Integration Tests → mvn verify -DskipUnitTests=true
├── 5. ✅ Code Coverage    → mvn jacoco:report -B  ← 🚦 QUALITY GATE
├── 6. ✅ Static Analysis  → mvn checkstyle:check  ← 🚦 QUALITY GATE
├── 7. ✅ Quality Gate     → Verifica mínimo 80% cobertura + 0 errores
├── 8. ✅ Docker Build     → docker build -t quality-gates-api:N .
└── 9. ✅ Deploy           → docker run qg_api_deployed -p 8082:8081
```

---

### 📸 IMAGEN 5 — Pipeline corriendo (Stage View)

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Ejecuta el pipeline con "Build Now"
> 2. Mientras el pipeline corre, haz clic en el build número en "Build History"
> 3. Ve a la vista principal del job donde se muestra el "Stage View"
> 4. Toma una captura cuando el pipeline esté en plena ejecución (algunos stages en verde, uno en azul/corriendo)
>
> **Lo que debe verse:** El Stage View de Jenkins con las columnas de cada stage coloreadas (verde = completado, azul = corriendo)

```
[ INSERTAR AQUÍ: Captura del Stage View de Jenkins mientras el pipeline se ejecuta ]
```

---

### 📸 IMAGEN 6 — Console Output del Pipeline

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Ve al build en ejecución → `Console Output`
> 2. Espera a que aparezcan los emojis del Jenkinsfile: `📥 Descargando código fuente...`, `🔨 Compilando el proyecto...`, etc.
> 3. Toma la captura mostrando el log con los emojis y pasos claramente visibles
>
> **Lo que debe verse:** La consola de Jenkins con los mensajes en español y emojis de cada etapa

```
[ INSERTAR AQUÍ: Captura del Console Output mostrando los pasos del pipeline ]
```

---

## 8. PASO 5 — Quality Gate: Pruebas Unitarias

### ¿Qué valida?

Este Quality Gate verifica que **todos los tests pasen** antes de continuar.

```groovy
stage('Unit Tests') {
    steps {
        sh 'mvn test -B'         // Ejecuta todos los *Test.java
    }
    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml'
        }
    }
}
```

### Cobertura de tests en el proyecto

| Clase | Tests |
|-------|-------|
| `ProductController` | GET all, GET by ID, POST, PUT, DELETE, GET by category, search |
| `ProductService` | getAllProducts, getById (found/not found), createProduct, updateProduct, deleteProduct |
| `Integration` | Test end-to-end con H2 en memoria |

### Salida esperada en consola

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### 📸 IMAGEN 7 — Resultados de Tests en Jenkins

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Después de que el pipeline termine, ve al build → `Test Result`
> 2. Jenkins mostrará el reporte de JUnit con todos los tests en VERDE
> 3. Toma la captura de la página de resultados de tests mostrando 0 failures
> 4. Alternativamente, puedes capturar la sección "Test Results" en el resumen del build
>
> **Lo que debe verse:** Tabla de resultados de tests con "Tests: X, Failures: 0, Errors: 0"

```
[ INSERTAR AQUÍ: Captura de los Test Results en Jenkins (JUnit report) ]
```

---

## 9. PASO 6 — Quality Gate: Cobertura JaCoCo

### ⭐ Este es el Quality Gate más importante para la presentación

### ¿Qué valida?

JaCoCo mide qué porcentaje del código fue ejecutado durante los tests.

**Reglas configuradas en `pom.xml`:**
```xml
<rules>
    <rule>
        <element>BUNDLE</element>
        <limits>
            <!-- Mínimo 80% de líneas cubiertas -->
            <limit>
                <counter>LINE</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.80</minimum>   <!-- ← 80% mínimo -->
            </limit>
            <!-- Mínimo 70% de ramas cubiertas -->
            <limit>
                <counter>BRANCH</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.70</minimum>   <!-- ← 70% mínimo -->
            </limit>
        </limits>
    </rule>
</rules>
```

### Comportamiento del Quality Gate JaCoCo

```
Si cobertura >= 80%  →  ✅ Quality Gate APROBADO  →  Pipeline continúa
Si cobertura < 80%   →  ❌ Quality Gate RECHAZADO  →  Pipeline FALLA
```

### Cómo ver el reporte JaCoCo en Jenkins

Después del pipeline exitoso, en el job verás el link:
**"JaCoCo Coverage Report"** → Este link lleva al HTML con el reporte detallado.

Estructura del reporte:
```
JaCoCo Coverage Report
├── com.qualitygates.api.controller  ← % cobertura del controller
├── com.qualitygates.api.service     ← % cobertura del servicio
├── com.qualitygates.api.model       ← % cobertura del modelo
└── com.qualitygates.api.repository  ← % cobertura del repositorio
```

---

### 📸 IMAGEN 8 — Reporte JaCoCo en Jenkins (el más importante)

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Espera a que el pipeline termine exitosamente
> 2. En el job de Jenkins, haz clic en **"JaCoCo Coverage Report"** (aparece en el menú lateral del build)
> 3. Verás el reporte HTML con barras de progreso en verde mostrando el % de cobertura
> 4. Toma la captura de la **página principal del reporte JaCoCo** mostrando el resumen general
> 5. También captura el detalle de alguna clase (ej: `ProductService`) mostrando las líneas cubiertas en verde
>
> **Lo que debe verse:** 
> - Barra de progreso en VERDE mostrando ≥80% de cobertura de líneas
> - Tabla con el breakdown por paquete/clase
> - Las "Instructions covered" y "Branches covered" en verde

```
[ INSERTAR AQUÍ: Captura del Reporte HTML de JaCoCo mostrando cobertura ≥80% ]
```

---

### 📸 IMAGEN 9 — Detalle de cobertura por clase en JaCoCo

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Dentro del reporte JaCoCo, haz clic en el paquete `com.qualitygates.api.service`
> 2. Luego haz clic en `ProductService`
> 3. Verás el código fuente con líneas en VERDE (cubiertas) y ROJO (no cubiertas)
> 4. Toma la captura mostrando el código del servicio con las líneas verdes
>
> **Lo que debe verse:** El código Java del servicio con la coloración de cobertura (verde = cubierto por tests)

```
[ INSERTAR AQUÍ: Captura del detalle de JaCoCo mostrando el código coloreado por cobertura ]
```

---

## 10. PASO 7 — Quality Gate: Análisis Estático Checkstyle

### ¿Qué valida?

Checkstyle analiza el código estáticamente para detectar:

| Categoría | Regla |
|-----------|-------|
| 📛 Naming | Clases en `PascalCase`, métodos en `camelCase`, constantes en `UPPER_CASE` |
| 📦 Imports | Sin imports no usados, sin `import *`, sin imports redundantes |
| 🏗️ Estructura | Llaves `{}` obligatorias, bloques vacíos prohibidos |
| 📏 Complejidad | Máx. complejidad ciclomática: **10**, máx. líneas por método: **50** |
| 🔤 Espacios | Espacios correctos alrededor de operadores y después de palabras clave |
| 📄 Archivos | Máx. 500 líneas por archivo, solo espacios (sin tabs) |

Estas reglas están en `checkstyle.xml` y están basadas en **Google Java Style Guide**.

### Comportamiento del Quality Gate Checkstyle

```groovy
stage('Static Analysis - Checkstyle') {
    steps {
        sh 'mvn checkstyle:check -B'  // Falla si hay CUALQUIER error
    }
}
```

```
Si errores Checkstyle = 0  →  ✅ Quality Gate APROBADO
Si errores Checkstyle > 0  →  ❌ Quality Gate RECHAZADO → Pipeline FALLA
```

### Ver el reporte Checkstyle

Después del pipeline: en el job verás **"Checkstyle Report"** → HTML con todos los hallazgos.

---

### 📸 IMAGEN 10 — Reporte Checkstyle en Jenkins

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Después del pipeline exitoso, ve al build → clic en **"Checkstyle Report"** (menú lateral)
> 2. Verás el reporte HTML con 0 errores de estilo
> 3. Toma la captura del reporte mostrando "No violations found" o las estadísticas en 0
>
> **Lo que debe verse:** El reporte Checkstyle con 0 errores/warnings de estilo de código

```
[ INSERTAR AQUÍ: Captura del Checkstyle Report en Jenkins mostrando 0 violations ]
```

---

## 11. PASO 8 — Docker Build & Deploy

### ¿Qué hace esta etapa?

Solo si los 3 Quality Gates están aprobados, Jenkins procede a:

1. **Construir** la imagen Docker usando el multi-stage `Dockerfile`
2. **Tagear** la imagen con el número de build y como `latest`
3. **Detener** el contenedor anterior si existe
4. **Desplegar** el nuevo contenedor en el puerto 8082

### El Dockerfile (Multi-Stage Build)

```
Dockerfile:

Stage 1: maven:3.9-eclipse-temurin-21 (Builder)
    └── mvn clean package -DskipTests -B
    └── Produce: target/quality-gates-api-1.0.0.jar

Stage 2: eclipse-temurin:21-jre-alpine (Runtime)
    └── Solo el JRE (imagen más liviana)
    └── Agrega: user no-root (seguridad)
    └── EXPOSE 8081
    └── HEALTHCHECK cada 30s
```

**Ventaja del multi-stage:** La imagen final es ~200MB vs ~600MB si usara el builder completo.

### Comandos ejecutados por Jenkins

```bash
# Build de la imagen
docker build -t quality-gates-api:${BUILD_NUMBER} .
docker tag quality-gates-api:${BUILD_NUMBER} quality-gates-api:latest

# Deploy del contenedor
docker stop qg_api_deployed 2>/dev/null || echo "No container to stop"
docker rm qg_api_deployed 2>/dev/null || echo "No container to remove"
docker run -d --name qg_api_deployed \
    -p 8082:8081 \
    -e MYSQL_HOST=host.docker.internal \
    -e MYSQL_PORT=3307 \
    -e MYSQL_DB=quality_gates_db \
    -e MYSQL_USER=root \
    -e MYSQL_PASSWORD=root123 \
    quality-gates-api:latest
```

---

### 📸 IMAGEN 11 — Docker Build en el Console Output

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Ve al Console Output del pipeline mientras corre el stage "Docker Build"
> 2. Verás el log del `docker build` con las capas de la imagen siendo construidas
> 3. Busca las líneas `Step 1/N`, `Step 2/N`, etc. y la línea `Successfully built <hash>`
> 4. Toma la captura de esa sección del log
>
> **Lo que debe verse:** El log de `docker build` con todos los steps completados y el hash de la imagen generada

```
[ INSERTAR AQUÍ: Captura del Console Output mostrando el docker build exitoso ]
```

---

### 📸 IMAGEN 12 — Contenedor desplegado en Docker Desktop

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Después del deploy, abre Docker Desktop
> 2. Verás el contenedor `qg_api_deployed` corriendo en el puerto 8082
> 3. También deberían verse `qg_mysql`, `qg_jenkins`, `qg_api`
> 4. Toma la captura mostrando los 4 contenedores activos
>
> **Lo que debe verse:** Docker Desktop con `qg_api_deployed` corriendo en puerto 8082 (junto a los otros contenedores)

```
[ INSERTAR AQUÍ: Captura de Docker Desktop mostrando qg_api_deployed en estado Running ]
```

---

## 12. PASO 9 — Verificar la API desplegada

### Endpoints disponibles

La API estará disponible en: `http://localhost:8082`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/products` | Listar todos los productos |
| `GET` | `/api/v1/products/{id}` | Obtener producto por ID |
| `POST` | `/api/v1/products` | Crear un producto nuevo |
| `PUT` | `/api/v1/products/{id}` | Actualizar un producto |
| `DELETE` | `/api/v1/products/{id}` | Eliminar un producto |
| `GET` | `/api/v1/products/category/{cat}` | Filtrar por categoría |
| `GET` | `/api/v1/products/search?name=X` | Buscar por nombre |
| `GET` | `/actuator/health` | Health check de la aplicación |

### Verificar con curl (o Postman)

```bash
# Health check
curl http://localhost:8082/actuator/health

# Respuesta esperada:
# {"status":"UP","components":{"db":{"status":"UP"}}}

# Crear un producto de prueba
curl -X POST http://localhost:8082/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Producto Demo",
    "description": "Creado para la presentación",
    "price": 99.99,
    "quantity": 10,
    "category": "DEMO"
  }'

# Listar todos los productos
curl http://localhost:8082/api/v1/products
```

---

### 📸 IMAGEN 13 — API respondiendo correctamente

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> **Opción A (Postman):**
> 1. Abre Postman
> 2. Crea una petición GET a `http://localhost:8082/api/v1/products`
> 3. Haz clic en Send
> 4. Captura la respuesta con código `200 OK` y el JSON de los productos
>
> **Opción B (Navegador):**
> 1. Abre `http://localhost:8082/actuator/health` en el navegador
> 2. Captura la respuesta JSON mostrando `"status": "UP"`
>
> **Lo que debe verse:** La API respondiendo con HTTP 200 y el JSON de respuesta

```
[ INSERTAR AQUÍ: Captura de Postman o navegador con la API respondiendo en puerto 8082 ]
```

---

## 13. Resultado Final — Pipeline Exitoso

### Stage View completo

Cuando todos los Quality Gates pasan, el Stage View debe verse así:

```
Stages:    Checkout  Build  Unit Tests  Integration  Coverage  Checkstyle  QG    Docker  Deploy
Status:      ✅        ✅       ✅           ✅           ✅         ✅        ✅      ✅      ✅
Duration:   2s       15s     30s          45s          10s        5s       20s    60s     10s
```

### Mensaje en el Console Output al finalizar

```
╔══════════════════════════════════════════╗
║  ✅ PIPELINE EXITOSO                     ║
║  ✅ Todos los Quality Gates aprobados     ║
║  ✅ Aplicación desplegada correctamente   ║
╚══════════════════════════════════════════╝

Finished: SUCCESS
```

---

### 📸 IMAGEN 14 — Pipeline completamente exitoso (la foto de la presentación)

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Espera a que el pipeline termine con todos los stages en VERDE
> 2. Ve a la vista principal del job `quality-gates-pipeline`
> 3. El Stage View mostrará todas las columnas en verde
> 4. También se verá el icono azul (éxito) en "Build History"
> 5. Toma la captura de **toda la pantalla** del job mostrando el Stage View completo en verde
>
> **Lo que debe verse:** Todos los stages del pipeline en color VERDE, con el mensaje "Last build: Success"
>
> ⭐ **Esta es la captura más importante para la presentación**

```
[ INSERTAR AQUÍ: Captura del Stage View completo con TODOS los stages en verde → "Pipeline Success" ]
```

---

### 📸 IMAGEN 15 — Mensaje de éxito en el Console Output

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Ve al build exitoso → `Console Output`
> 2. Baja hasta el final del log
> 3. Verás el banner `╔══════════════════════════════╗ ✅ PIPELINE EXITOSO` y abajo `Finished: SUCCESS`
> 4. Toma la captura del final del log mostrando el mensaje de éxito
>
> **Lo que debe verse:** El banner ASCII con los checks verdes y "Finished: SUCCESS" al final del log

```
[ INSERTAR AQUÍ: Captura del final del Console Output con "Finished: SUCCESS" ]
```

---

## 14. Escenario de Fallo — Quality Gate rechaza el código

### ⭐ Este escenario es ideal para demostrar el valor de los Quality Gates

### ¿Cómo simular el fallo?

Para demostrar que los Quality Gates SÍ funcionan, puedes simular un fallo temporalmente.

**Opción A — Bajar el umbral de cobertura requerida:**

Edita `pom.xml` temporalmente, cambia el mínimo de cobertura:

```xml
<!-- ANTES (pasa): -->
<minimum>0.80</minimum>

<!-- PARA DEMOSTRAR FALLO, cambia a un valor imposible: -->
<minimum>0.99</minimum>
```

**Opción B — Introducir error de Checkstyle:**

Agrega un import no usado en cualquier archivo Java:

```java
// Agregar este import que Checkstyle detectará como "unused"
import java.util.Date;  // ← Import no utilizado → Checkstyle falla
```

### Mensaje de fallo en el Console Output

```
[ERROR] Failed to execute goal org.jacoco:jacoco-maven-plugin:check (check)
[ERROR] Rule violated for bundle quality-gates-api:
[ERROR]   - Line coverage ratio is 0.76, but expected minimum is 0.99

╔══════════════════════════════════════════╗
║  ❌ PIPELINE FALLIDO                     ║
║  ❌ Quality Gate NO aprobado              ║
║  ❌ Revisar los reportes de errores       ║
╚══════════════════════════════════════════╝

Finished: FAILURE
```

---

### 📸 IMAGEN 16 — Pipeline fallido por Quality Gate (para la presentación)

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Temporalmente modifica `pom.xml` cambiando `<minimum>0.80</minimum>` a `<minimum>0.99</minimum>`
> 2. Ejecuta el pipeline nuevamente
> 3. El stage "Quality Gate" (o "Code Coverage") fallará en ROJO
> 4. Toma la captura del Stage View mostrando el stage fallido en ROJO
> 5. **Restaura el archivo original** después de tomar la captura
>
> **Lo que debe verse:** El Stage View con el stage "Quality Gate" en ROJO y los stages anteriores en verde. Esto es la "prueba" de que los Quality Gates funcionan como barrera de calidad.

```
[ INSERTAR AQUÍ: Captura del Stage View con un stage en ROJO (Quality Gate falla) ]
```

---

### 📸 IMAGEN 17 — Error detallado en el Console Output (fallo del Quality Gate)

> **📷 CÓMO OBTENER ESTA IMAGEN:**
> 1. Con el pipeline fallido, ve al Console Output
> 2. Busca el mensaje de error de JaCoCo: `Rule violated for bundle...`
> 3. Toma la captura mostrando el mensaje de error específico de JaCoCo o Checkstyle
> 4. Y el banner final `❌ PIPELINE FALLIDO`
>
> **Lo que debe verse:** El mensaje de error explicando por qué falló el Quality Gate y el banner rojo al final

```
[ INSERTAR AQUÍ: Captura del Console Output con el error del Quality Gate y "Finished: FAILURE" ]
```

---

## 15. Resumen para la Presentación

### El Mensaje Central

> **"Los Quality Gates son la barrera automática que separa el código de calidad del código con errores. Ningún desarrollador puede 'saltarse' esta validación."**

### Flujo completo de la demo (guión para presentar)

```
1. "Aquí tenemos nuestra API de productos en Spring Boot"
   → Mostrar el código en VS Code (ProductController, ProductService)

2. "El código tiene pruebas unitarias que cubren todos los casos"
   → Mostrar las clases de test

3. "Ahora levantamos el entorno completo con un solo comando"
   → Ejecutar: docker compose up -d
   → IMAGEN 1: Docker Desktop con los 3 contenedores

4. "Jenkins es nuestro servidor de CI/CD"
   → Abrir http://localhost:8080
   → IMAGEN 2: Dashboard de Jenkins

5. "Ejecutamos el pipeline"
   → Clic en Build Now
   → IMAGEN 5: Stage View corriendo

6. "Watch the Quality Gates in action"
   → IMAGEN 7: Tests pasando (0 failures)
   → IMAGEN 8: JaCoCo ≥80% cobertura ✅
   → IMAGEN 10: Checkstyle 0 violations ✅

7. "Pipeline exitoso — la aplicación fue desplegada automáticamente"
   → IMAGEN 14: Todos los stages en verde ⭐

8. "¿Qué pasa si el código no cumple los estándares? Vamos a demostrarlo"
   → Mostrar el fallo simulado
   → IMAGEN 16: Stage en rojo ❌

9. "El pipeline se detuvo. El código no llegó a producción."
   → IMAGEN 17: Error en console output

10. "Eso es el poder de los Quality Gates"
    → Conclusión
```

### Checklist de la Demo

```
Antes de la presentación:
[ ] Docker Desktop está corriendo
[ ] docker compose up -d → todos los contenedores en verde
[ ] Jenkins configurado: Maven-3.9 en Tools
[ ] Job quality-gates-pipeline creado y configurado
[ ] Pipeline ejecutado al menos una vez exitosamente (para que Stage View tenga historial)
[ ] Tener el escenario de fallo listo (pom.xml con 0.99 comentado o guardado en otro archivo)
[ ] API respondiendo en http://localhost:8082/actuator/health

Durante la presentación:
[ ] Abrir Docker Desktop en segundo plano
[ ] Abrir Jenkins en tab #1: http://localhost:8080
[ ] Abrir la API en tab #2: http://localhost:8082/api/v1/products
[ ] Tener VS Code con el código abierto
```

### Arquitectura de calidad — Resumen visual

```
┌──────────────────────────────────────────────────────────────┐
│             PIPELINE DE QUALITY GATES                        │
│                                                              │
│  Code    →  Build  →  Tests  →  🚦 GATES  →  Docker  → PROD │
│                                                              │
│                         🔴 STOP                              │
│                    ┌────────────┐                            │
│  Si falla CUALQUIER Quality Gate:                            │
│  · El pipeline se detiene                                    │
│  · El código NO avanza                                       │
│  · El equipo recibe notificación                             │
│  · Se debe corregir y volver a intentar                      │
│                    └────────────┘                            │
│                                                              │
│  QG-1: Tests     → 100% deben pasar                         │
│  QG-2: JaCoCo    → Mínimo 80% cobertura de líneas           │
│                    Mínimo 70% cobertura de ramas             │
│  QG-3: Checkstyle→ 0 violaciones de estilo                  │
└──────────────────────────────────────────────────────────────┘
```

---

## 📚 Referencias Técnicas

| Componente | Versión | Rol |
|------------|---------|-----|
| Java | 21 (LTS) | Lenguaje de programación |
| Spring Boot | 3.2.5 | Framework de la API REST |
| Maven | 3.9.x | Build tool y gestión de dependencias |
| JaCoCo | 0.8.12 | Cobertura de código (Quality Gate) |
| Checkstyle | 3.3.1 | Análisis estático (Quality Gate) |
| MySQL | 8.4 | Base de datos de producción |
| H2 | embedded | Base de datos para tests |
| Docker | 24+ | Contenedores y despliegue |
| Jenkins | LTS JDK 21 | Servidor de CI/CD |

---

*Documento generado para la presentación del proyecto Quality Gates API*  
*Fecha: Abril 2026*
