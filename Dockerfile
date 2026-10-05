# build stage, compiles the app inside a container so you don't need maven installed to build it
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy the maven wrapper and pom first, so docker can cache the dependency download
# as long as pom.xml doesn't change, rebuilds skip straight past this step
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# now copy the source and build the jar, skipping tests since they run separately in CI
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# run stage, starts from a smaller jre image since we only need to run the jar, not compile it
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]