FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends \
    tesseract-ocr tesseract-ocr-chi-sim tesseract-ocr-eng \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /build/target/ocr-cloud-service-1.0.0.jar app.jar
ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
