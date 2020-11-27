FROM java:8
EXPOSE 8080
ARG JAR_FILE=/home/runner/work/Integration/Integration/target/spring-social-1.0.0.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-jar","/app.jar"]
