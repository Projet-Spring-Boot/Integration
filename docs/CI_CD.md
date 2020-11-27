# Mise en place de la CI CD

## Description

### Introduction

Dans l'optique d'intégration et déployement perpétuel nous allons devoir mettre en place de nombreuses choses. Nous aurons besoin dans cette partie d'un compte [AWS](http://www.awseducate.com/), d'un compte [Docker](https://www.docker.com/), d'accès au repository [Github](https://github.com/Projet-Spring-Boot/).

### Utilisation

Notre produit sera composé de 2 micro-services. Le µS Adminstration et le µS Interface. La team Intégration fournira a ces 2 micro-services le fichier `build.yaml` et le `Dockerfile`.
Ainsi a chaque push sur la branch `Deploy` une image Docker sera crée et push sur un repo commun au projet. De la team Intégration récupérera les Docker images pour les envoyer dans AWS.

## CI: Intégration Continue

### Dockerfile

Pour créer l'image Docker nous devons renseinger les paramètres qui permettrons de la créer. Ces informations sont rassemblées dans un fichier `Dockerfile`.

Construction de `Dockerfile`
```
FROM java:8
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-jar","/app.jar"]
```


### Github Action

Les Github Actions vont nous permettre d'automatiser l'intégration de notre code.

Pour créer une G.A. nous allons commencer par créer un dossier `.github/workflows`. Dans ce dossier nous créons un fichier `build.yaml`. Il faut ensuite créer le repository Docker qui stockera notre image Docker. Enfin il faudra ajouter des *secrets* dans notre github pour renseigner le *username* et le *password* de Docker.

Construction de `build.yaml`:
```yaml
name: Continuous Integration

on:
  push:
    branches: [ deploy ]

jobs:
  deploy:
    name: Integration
    runs-on: ubuntu-latest
    steps:

      - name: Checkout master branch
        uses: actions/checkout@v2

      - name: Setup JDK 1.8
        uses: actions/setup-java@v1
        with:
          java-version: 1.8

      - name: Maven Clean & Package
        run: mvn -B clean package --file pom.xml

      - name: Docker Build & Push
        uses: docker/build-push-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
          repository: <your_docker_id>/<your_micro_service>
          tag_with_ref: true
          tag_with_sha: true
```

Le step "Setup JDK 1.8" permet de configurer notre environnement JAVA.

Le step "Maven Clean & Package" permet de recréer les dépendances et produit un fichier `.jar`.

Le step "Docker Build & Push" permet de créer et de télécharger l'image Docker sur notre repository Docker.

:warning: Il est très important de créer les secrets dans github :warning:

## CD: Déploient Continue

