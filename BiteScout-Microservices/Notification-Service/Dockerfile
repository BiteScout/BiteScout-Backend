FROM arm64v8/openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8030
ENTRYPOINT ["java","-jar","app.jar"] 