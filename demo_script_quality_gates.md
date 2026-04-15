# 🎬 Guión Completo — Demo Quality Gates API
### *CI/CD con Jenkins + Docker + JaCoCo + Checkstyle + Ngrok*

> **Proyecto:** `quality-gates-api` · **Duración estimada:** 10–12 minutos  
> **Objetivo:** Demostrar que los Quality Gates garantizan que **solo el código de calidad llega a producción**

---

## ✅ CHECKLIST PRE-DEMO (Hacer esto ANTES de entrar al salón)

```
[ ] Docker Desktop abierto y corriendo
[ ] docker compose up -d → los 3 contenedores en estado "Running"
[ ] Jenkins configurado: usuario admin, Maven-3.9 en Tools
[ ] Job "quality-gates-pipeline" creado (puede ya tener builds anteriores)
[ ] Pipeline ejecutado al menos 1 vez exitosamente (Stage View tiene historial)
[ ] pom.xml CON el valor 0.80 (modo exitoso)
[ ] pom.xml guardado también con 0.99 en un bloque comentado (para el fallo)
[ ] Ngrok apuntando a puerto 8080 (Jenkins) y a puerto 8082 (API desplegada)
[ ] VS Code abierto con ProductController.java, ProductService.java y ProductServiceTest.java visibles
[ ] Tabs del navegador preparados:
      Tab 1: http://localhost:8080  (Jenkins dashboard)
      Tab 2: URL de Ngrok apuntando a Jenkins (para acceso externo)
      Tab 3: http://localhost:8082/api/v1/products (API desplegada)
      Tab 4: URL de Ngrok apuntando a la API (para acceso externo)
```

---

## 🎬 ESCENA 1 — Introducción: ¿Qué son los Quality Gates?
**⏱ Duración: ~1 min**

### 🗣️ Diálogo
> *"Antes de entrar al código, quiero responder una pregunta: ¿Qué pasa cuando un desarrollador sube código con bugs, sin pruebas o que no sigue los estándares de la empresa?"*  
> *"Normalmente ese código llega a producción, y los problemas aparecen cuando ya es demasiado tarde.*  
> *Los **Quality Gates** son barreras automáticas dentro del pipeline de CI/CD que **bloquean** cualquier código que no cumpla los estándares. Hoy vamos a ver eso en acción."*

### 📊 Mostrar en pantalla (VS Code o diapositiva)
```
┌─────────────────────────────────────────────────────────┐
│                  PIPELINE CON QUALITY GATES             │
│                                                         │
│  Código  →  Build  →  Tests  →  🚦 GATES  →  Producción │
│                                    ↓                   │
│                          Si CUALQUIERA falla:           │
│                          ❌ El pipeline se DETIENE       │
│                          ❌ El código NO llega a PROD    │
│                                                         │
│  QG-1: Pruebas unitarias  → 100% deben pasar            │
│  QG-2: Cobertura JaCoCo   → Mínimo 80% de líneas        │
│  QG-3: Checkstyle         → 0 violaciones de estilo     │
└─────────────────────────────────────────────────────────┘
```

---

## 🎬 ESCENA 2 — Mostrar el código de la API
**⏱ Duración: ~1.5 min**

### 🗣️ Diálogo
> *"Aquí tenemos nuestra API de productos construida con **Spring Boot 3** y **Java 21**. Es una API REST completa con 7 endpoints: crear, listar, actualizar, eliminar, buscar por nombre y filtrar por categoría."*

### 💻 Acción — Mostrar en VS Code: `ProductController.java`

> *"Este es el controller. Cada endpoint valida los datos, delega al servicio y regresa una respuesta estructurada con los códigos HTTP correctos."*

### 💻 Acción — Cambiar a `ProductService.java`

> *"Aquí está la lógica de negocio. El servicio lanza excepciones personalizadas cuando no encuentra un recurso, que el controller convierte en un 404 correcto."*

---

## 🎬 ESCENA 3 — Mostrar los tests (Quality Gate 1)
**⏱ Duración: ~1.5 min**

### 🗣️ Diálogo
> *"Pero el código solo es tan bueno como sus pruebas. Vamos a ver el primer Quality Gate: **las pruebas unitarias**."*

### 💻 Acción — Mostrar en VS Code: `ProductServiceTest.java`

> *"Tenemos pruebas para cada método del servicio: el camino feliz, los errores esperados, los casos borde. Este es el primer Quality Gate: **si cualquier test falla, el pipeline se detiene** y el código no avanza."*

### 📊 Tabla de cobertura de tests
| Clase | Tests cubiertos |
|-------|----------------|
| `ProductController` | GET all, GET byId, POST, PUT, DELETE, GET by category, search |
| `ProductService` | getAllProducts, getById (found/not found), create, update, delete |
| `Integration` | Test end-to-end con base de datos H2 en memoria |

> *"**15 tests en total** — todos deben pasar. Es la primera barrera."*

---

## 🎬 ESCENA 4 — Levantar el entorno Docker
**⏱ Duración: ~1 min**

### 🗣️ Diálogo
> *"Ahora vamos a levantar todo el entorno. Con un solo comando levantamos **tres contenedores**: la base de datos MySQL, el servidor Jenkins y nuestra aplicación."*

### 💻 Acción — Ejecutar en terminal

```bash
cd C:\Users\ASUS I5\Documents\quality-gates-api
docker compose up -d
docker compose ps
```

### ✅ Salida esperada en terminal
```
NAME          IMAGE                       STATUS          PORTS
qg_mysql      mysql:8.4                   Up (healthy)    0.0.0.0:3307->3306/tcp
qg_jenkins    jenkins/jenkins:lts-jdk21   Up              0.0.0.0:8080->8080/tcp
qg_api        quality-gates-api-api       Up              0.0.0.0:8081->8081/tcp
```

### 💻 Acción — Mostrar Docker Desktop con los 3 contenedores en verde
> *"Perfecto. Todo corriendo. Esta es nuestra arquitectura de contenedores: MySQL, Jenkins y la API conviviendo en la misma red Docker."*

> 📸 **IMAGEN 1:** Docker Desktop con los 3 contenedores en estado `Running`

---

## 🎬 ESCENA 5 — Ngrok: Acceso externo al pipeline
**⏱ Duración: ~1 min**

### 🗣️ Diálogo
> *"Un detalle importante: normalmente Jenkins corre en `localhost` — solo yo puedo verlo desde esta máquina. Pero con **Ngrok** exponemos el puerto de Jenkins y la API al mundo exterior. Esto nos permite mostrar el pipeline desde cualquier dispositivo, o integrarlo con webhooks de GitHub desde internet."*

### 💻 Acción — Abrir terminal y verificar Ngrok (ya instalado)

```bash
# Verificar que Ngrok está instalado
ngrok version

# Exponer Jenkins (puerto 8080) — en una terminal separada
ngrok http 8080

# ─── En OTRA terminal ───
# Exponer la API desplegada (puerto 8082)
ngrok http 8082
```

### ✅ Salida esperada de Ngrok

```
Session Status      online
Account             tu-cuenta@email.com (Plan: Free)
Version             3.x.x
Forwarding          https://abc123.ngrok-free.app -> http://localhost:8080
                    ─────────────────────────────────────────────────────
                    https://xyz789.ngrok-free.app -> http://localhost:8082
```

### 🗣️ Diálogo (continuación)
> *"Ahora cualquier persona en esta sala puede acceder a Jenkins desde su celular usando esa URL de Ngrok. Eso también sirve para configurar webhooks de GitHub que disparen el pipeline automáticamente cada vez que alguien hace un `git push`."*

> 📸 **IMAGEN NGROK:** La terminal mostrando la URL pública de Ngrok apuntando a Jenkins y la API

> **💡 Tip presentación:** Muestra la URL de Ngrok en el proyector y pide a alguien del público que la abra en su celular para demostrar el acceso externo.

---

## 🎬 ESCENA 6 — Acceder a Jenkins y lanzar el pipeline
**⏱ Duración: ~1 min**

### 🗣️ Diálogo
> *"Este es Jenkins, nuestro servidor de CI/CD. Cuando un desarrollador sube código, Jenkins automáticamente descarga el código, lo construye, ejecuta todas las pruebas y aplica los Quality Gates — sin intervención humana."*

### 💻 Acción — Abrir en navegador

```
http://localhost:8080
# — O desde Ngrok —
https://abc123.ngrok-free.app
```

> *"Aquí está el job `quality-gates-pipeline`. Voy a hacer clic en **Build Now** para iniciar el pipeline."*

### 💻 Acción — Clic en `Build Now`

> *"Podemos ver cómo empieza a correr en el Stage View. Cada columna es una etapa del pipeline."*

> 📸 **IMAGEN 2:** Dashboard de Jenkins con el job listo  
> 📸 **IMAGEN 5:** Stage View con el pipeline en ejecución (algunas columnas en azul/corriendo)

---

## 🎬 ESCENA 7 — Las etapas del pipeline (recorrido)
**⏱ Duración: ~1.5 min**

### 🗣️ Diálogo
> *"Este pipeline tiene **9 etapas**. Las primeras son preparatorias, luego vienen los tres Quality Gates, y solo si los 3 pasan, se construye y despliega la aplicación."*

### 📊 Mostrar el flujo de etapas
```
Stage View — quality-gates-pipeline
─────────────────────────────────────────────────────────────────────
  Checkout  →  Build  →  Unit Tests  →  Integration  →  Code Coverage
     ✅           ✅          ✅              ✅               ✅
              (mvn compile) (mvn test)   (mvn verify)   (jacoco:report)

  →  Static Analysis  →  🚦 Quality Gate  →  Docker Build  →  Deploy
           ✅                   ✅                 ✅             ✅
       (checkstyle)        (jacoco:check         (docker        (docker
                           checkstyle:check)      build)          run)
─────────────────────────────────────────────────────────────────────
```

> *"El stage de **Quality Gate** es el corazón. Llama a `jacoco:check` — que verifica el mínimo de cobertura — y a `checkstyle:check` — que verifica las reglas de estilo. Si cualquiera de los dos falla, el pipeline se detiene aquí y nada más corre."*

---

## 🎬 ESCENA 8 — Quality Gate 1: Pruebas unitarias pasan
**⏱ Duración: ~1 min**

### 🗣️ Diálogo
> *"El primer Quality Gate: las pruebas unitarias. Jenkins ejecuta `mvn test` y publica el resultado de JUnit. Vamos a ver el reporte."*

### 💻 Acción — Ir al build exitoso → `Test Result`
> *"15 tests. 0 fallos. 0 errores. Esta es la primera barrera que el código superó."*

> 📸 **IMAGEN 7:** Reporte JUnit en Jenkins — `Tests: 15, Failures: 0, Errors: 0`

### ⭐ Énfasis clave
> *"Si un solo test fallara, el pipeline se detiene AQUÍ. El código no puede avanzar. No hay forma de saltarse esto."*

---

## 🎬 ESCENA 9 — Quality Gate 2: Cobertura JaCoCo ≥ 80%
**⏱ Duración: ~2 min — Esta es la estrella de la presentación*

### 🗣️ Diálogo
> *"El segundo Quality Gate es el más poderoso: la cobertura de código con **JaCoCo**. JaCoCo instrumenta el bytecode de Java para detectar exactamente qué líneas y qué ramas del código fueron ejecutadas durante los tests."*

### 💻 Acción — Clic en `JaCoCo Coverage Report` (menú lateral del build)

> *"Aquí está el reporte. Vean estas barras verdes — representan el porcentaje de código cubierto. Tenemos más del 80% de cobertura de líneas y más del 70% de cobertura de ramas."*

> 📸 **IMAGEN 8:** Reporte JaCoCo con barras de progreso en verde ≥ 80%

### 💻 Acción — Hacer clic en el paquete `service` → `ProductService`

> *"Y si quiero ver el detalle, puedo ver exactamente qué líneas se ejecutaron. Las líneas en VERDE fueron ejercitadas por los tests. Las líneas en AMARILLO o ROJO son las que no tienen cobertura."*

> 📸 **IMAGEN 9:** Código Java con coloración verde/rojo de JaCoCo

### ⭐ Énfasis clave — Mostrar la regla en `pom.xml`

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.80</minimum>  ← Esta línea es el Quality Gate
</limit>
```

> *"Esta línea en el `pom.xml` es la definición del Quality Gate. El mínimo es **0.80 — 80%**. Si el reporte de JaCoCo devuelve menos, Maven lanza una excepción y el pipeline falla inmediatamente. Es objetivo. Es automático. No depende de la opinión de nadie."*

---

## 🎬 ESCENA 10 — Quality Gate 3: Checkstyle — 0 violaciones
**⏱ Duración: ~1.5 min**

### 🗣️ Diálogo
> *"El tercer Quality Gate es el análisis estático con **Checkstyle**. Mientras JaCoCo mide cuánto código se prueba, Checkstyle mide si el código está bien escrito según los estándares de la empresa."*

### 💻 Acción — Clic en `Checkstyle Report` (menú lateral del build)

> *"El reporte muestra **0 violaciones**. Nuestro código cumple con todas las reglas."*

> 📸 **IMAGEN 10:** Checkstyle Report con 0 violations

### 📊 Mostrar las reglas activas (del `checkstyle.xml`)

| Categoría | Regla aplicada |
|-----------|---------------|
| 📛 Naming | Clases en `PascalCase`, métodos en `camelCase` |
| 📦 Imports | Sin imports no usados, sin `import *` |
| 🏗️ Estructura | Llaves `{}` obligatorias en todos los bloques |
| 📏 Complejidad | Máx. complejidad ciclomática de 10 |
| 🔤 Espacios | Espacios correctos alrededor de operadores |
| 📄 Tamaño | Máx. 500 líneas por archivo, sin tabs |

> *"Estas reglas están basadas en el **Google Java Style Guide**. Si alguien escribe `import java.util.*;` o un método con más de 50 líneas, Checkstyle lo rechaza."*

---

## 🎬 ESCENA 11 — Pipeline exitoso: la aplicación fue desplegada
**⏱ Duración: ~1 min**

### 🗣️ Diálogo
> *"Los 3 Quality Gates han pasado. Ahora el pipeline procede automáticamente a construir la imagen Docker y desplegar la aplicación."*

### 💻 Acción — Volver al Stage View, mostrar todos los stages en verde

> 📸 **IMAGEN 14:** Stage View con TODOS los stages en verde ⭐

### 💻 Acción — Ir al Console Output final

```
╔══════════════════════════════════════════╗
║  ✅ PIPELINE EXITOSO                     ║
║  ✅ Todos los Quality Gates aprobados     ║
║  ✅ Aplicación desplegada correctamente   ║
╚══════════════════════════════════════════╝

Finished: SUCCESS
```

> 📸 **IMAGEN 15:** Banner de éxito en Console Output con "Finished: SUCCESS"

### 💻 Acción — Abrir la API desplegada en el navegador

```bash
# Local
curl http://localhost:8082/actuator/health
# → {"status":"UP"}

# Desde Ngrok (acceso externo)
https://xyz789.ngrok-free.app/api/v1/products
```

> *"Y aquí está la API corriendo en el puerto 8082, accesible también desde Ngrok. El código que ven aquí pasó las 3 barreras. Nada que no cumpla los estándares puede llegar a este punto."*

> 📸 **IMAGEN 13:** API respondiendo en el navegador con HTTP 200 + JSON

---

## 🎬 ESCENA 12 — Escenario de fallo: el poder de los Quality Gates
**⏱ Duración: ~1.5 min — La escena más impactante**

### 🗣️ Diálogo
> *"¿Pero qué pasa si alguien sube código que no cumple los estándares? Vamos a demostrarlo. Voy a simular que nuestras pruebas no cubren suficiente código — subiré el umbral requerido a 99%."*

### 💻 Acción — Editar `pom.xml` EN VIVO

```xml
<!-- CAMBIAR temporalmente para el demo de fallo -->
<minimum>0.80</minimum>   ← cambiar a →   <minimum>0.99</minimum>
```

### 💻 Acción — Ejecutar el pipeline nuevamente con **Build Now**

> *"Fíjense en el Stage View. El pipeline avanzó hasta... aquí. El stage de **Quality Gate** está en ROJO."*

> 📸 **IMAGEN 16:** Stage View con el stage "Quality Gate" en ROJO ❌

### 💻 Acción — Abrir el Console Output del build fallido

```
[ERROR] Failed to execute goal org.jacoco:jacoco-maven-plugin:check
[ERROR] Rule violated for bundle quality-gates-api:
[ERROR] - Line coverage ratio is 0.76, but expected minimum is 0.99

╔══════════════════════════════════════════╗
║  ❌ PIPELINE FALLIDO                     ║
║  ❌ Quality Gate NO aprobado              ║
║  ❌ Revisar los reportes de errores       ║
╚══════════════════════════════════════════╝

Finished: FAILURE
```

> 📸 **IMAGEN 17:** Console Output con el error de JaCoCo y "Finished: FAILURE"

### ⭐ Énfasis clave (el momento más importante del demo)
> *"El pipeline se detuvo. Las etapas de Docker Build y Deploy **nunca corrieron**. El código no llegó a producción. No hay excepción, no hay votación, no hay forma de saltarse el Quality Gate. Es automático y es objetivo."*

> *"Ahora voy a restaurar el umbral a 80%, que era el valor correcto, y el código vuelve a pasar."*

### 💻 Acción — Restaurar `pom.xml`

```xml
<minimum>0.80</minimum>  ← restaurar el valor correcto
```

---

## 🎬 ESCENA FINAL — Conclusión
**⏱ Duración: ~30 seg**

### 🗣️ Diálogo
> *"Esto es el poder de los Quality Gates: automatizar la calidad. No dependemos de que alguien recuerde revisar la cobertura, ni de que el code review sea exhaustivo. Las reglas están definidas en el repositorio, y el pipeline las aplica en cada commit, sin excepción."*

> *"Código que no pasa los Quality Gates no existe para producción. Así de simple."*

---

## 📋 RESUMEN DE IMÁGENES A CAPTURAR

| # | Imagen | Qué debe mostrar |
|---|--------|-----------------|
| IMAGEN 1 | Docker Desktop | 3 contenedores en verde (`qg_mysql`, `qg_jenkins`, `qg_api`) |
| IMAGEN 2 | Jenkins Dashboard | Panel principal de Jenkins en `localhost:8080` |
| IMAGEN NGROK | Terminal Ngrok | URLs públicas `https://xxx.ngrok-free.app` apuntando a :8080 y :8082 |
| IMAGEN 5 | Stage View corriendo | Algunas columnas en azul (en ejecución) |
| IMAGEN 7 | Test Results | `Tests: 15, Failures: 0, Errors: 0` |
| IMAGEN 8 | JaCoCo Report | Barras verdes con cobertura ≥ 80% ⭐ |
| IMAGEN 9 | JaCoCo detalle | Código Java coloreado verde/rojo por cobertura |
| IMAGEN 10 | Checkstyle Report | `0 violations` o `No violations found` |
| IMAGEN 13 | API respondiendo | HTTP 200 + JSON en navegador o Postman |
| IMAGEN 14 | Stage View exitoso | TODOS los stages en verde ⭐ |
| IMAGEN 15 | Console Output éxito | Banner `✅ PIPELINE EXITOSO` + `Finished: SUCCESS` |
| IMAGEN 16 | Stage View fallido | Stage "Quality Gate" en ROJO ❌ |
| IMAGEN 17 | Console Output fallo | Error JaCoCo + `Finished: FAILURE` |

---

## 🎯 MENSAJES CLAVE (para recordar durante la presentación)

1. **Quality Gates = barreras automáticas** → no dependen de humanos ni del azar
2. **Si cualquiera falla → el pipeline se para** → código no llega a producción
3. **JaCoCo mide CUÁNTO se prueba** (≥80% de líneas) → detecta código sin tests
4. **Checkstyle mide CÓMO está escrito** (0 violaciones) → estilo consistente del equipo
5. **Ngrok = acceso externo** → cualquiera puede ver el pipeline, y se pueden configurar webhooks automáticos
6. **Todo está en el repositorio** (`Jenkinsfile`, `pom.xml`, `checkstyle.xml`) → la calidad es versionada como el código

---

*Guión generado para la presentación del proyecto Quality Gates API — Abril 2026*
