FROM java:8
EXPOSE 8080
COPY target/spring-social-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
