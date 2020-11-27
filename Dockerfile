FROM java:8
EXPOSE 8080
COPY target/spring-social-1.0.0.jar app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-jar","/app.jar"]
