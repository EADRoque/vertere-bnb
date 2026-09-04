# Multi-stage build: compile in a full JDK image, run in a slim JRE-only
# image so the final container doesn't carry the Gradle wrapper, source,
# or a full JDK it never needs at runtime.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
# -Xmx caps heap so 8 of these can coexist comfortably on one small host
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]
