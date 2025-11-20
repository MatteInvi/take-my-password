# ------------------------
# 1) Costruzione con Maven
# ------------------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia tutto il progetto (pom.xml + src)
COPY . .

# Compila il progetto senza test
RUN mvn clean package -DskipTests

# ------------------------
# 2) Esecuzione del JAR
# ------------------------
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copia il jar dal primo container
COPY --from=build /app/target/*.jar app.jar

# Espone la porta 8080 (Spring Boot default)
EXPOSE 8080

# Avvia l'applicazione
ENTRYPOINT ["java", "-jar", "app.jar"]