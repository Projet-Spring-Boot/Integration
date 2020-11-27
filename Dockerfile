FROM java:8
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-social_1.0.0.jar
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-jar","/spring-social_1.0.0.jar"]
