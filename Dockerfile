# ---- Build Stage ----
FROM maven:3.8.6-amazoncorretto-17 AS build

WORKDIR /app

# Copy pom and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn package -DskipTests

# ---- Run Stage ----
FROM amazoncorretto:17

WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/infrareport-1.0.0.jar infrareport-1.0.0.jar

# Create uploads directory and set permissions
RUN mkdir -p /app/uploads && chmod -R 777 /app/uploads

# Expose uploads as a volume (for Docker Compose to mount)
VOLUME ["/app/uploads"]

EXPOSE 8080

# Set environment variables for Firebase (if needed)
ENV FIREBASE_TYPE="" \
    FIREBASE_PROJECT_ID="" \
    FIREBASE_PRIVATE_KEY_ID="" \
    FIREBASE_PRIVATE_KEY="" \
    FIREBASE_CLIENT_EMAIL="" \
    FIREBASE_CLIENT_ID="" \
    FIREBASE_AUTH_URI="" \
    FIREBASE_TOKEN_URI="" \
    FIREBASE_AUTH_PROVIDER_X509_CERT_URL="" \
    FIREBASE_CLIENT_X509_CERT_URL="" \
    FIREBASE_UNIVERSE_DOMAIN="" \
    FIREBASE_STORAGE_BUCKET=""

CMD ["java", "-jar", "infrareport-1.0.0.jar"]