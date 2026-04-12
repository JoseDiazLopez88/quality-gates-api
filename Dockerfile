# ========================================
# Stage 1: Build con Maven
# ========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar archivos de configuración primero (cache de dependencias)
COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar y ejecutar tests
RUN mvn clean package -DskipTests -B

# ========================================
# Stage 2: Runtime con JRE ligero
# ========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar JAR desde el builder
COPY --from=builder /app/target/*.jar app.jar

# Cambiar a usuario no-root
USER appuser

# Puerto de la aplicación
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8081/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
