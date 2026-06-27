FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml spotbugs-exclude.xml ./

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw package -DskipTests -B
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S catalogo && adduser -S catalogo -G catalogo
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
RUN chown catalogo:catalogo app.jar
USER catalogo

EXPOSE 8082

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
