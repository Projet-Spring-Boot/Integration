FROM java:8
EXPOSE 8080
WORKDIR /target
COPY *.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
