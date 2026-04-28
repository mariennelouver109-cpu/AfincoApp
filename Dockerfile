FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java","-jar","target/AfincoApp-0.0.1-SNAPSHOT.jar"]
