# ===== Этап 1: сборка =====
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Сначала копируем только pom.xml — слой закешируется,
# пока зависимости не изменятся
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Теперь копируем исходники и собираем
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B

# ===== Этап 2: рантайм =====
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Непривилегированный пользователь
RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/*.jar app.jar

RUN chown app:app app.jar
USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]