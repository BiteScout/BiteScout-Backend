FROM arm64v8/openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8222
ENTRYPOINT ["java","-jar","app.jar"] 