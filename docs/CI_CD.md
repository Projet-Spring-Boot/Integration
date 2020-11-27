# Mise en place de la CI CD

## Resources
Voici les fichiers en question:

- [build.yaml](/res/build.yaml)
- [Dockerfile](/res/Dockerfile)
- [sonar-project.properties](/res/sonar-project.properties)

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
    branches: [ master ] # Lorsque l'on push sur la branch master

jobs:
  deploy:
    name: Integration
    runs-on: ubuntu-latest
    steps:

      - name: Checkout master branch # On se place sur la branch master
        uses: actions/checkout@v2

      - name: Setup JDK 1.8 # On paramètre notre JDK
        uses: actions/setup-java@v1
        with:
          java-version: 1.8

      - name: Maven Clean & Package # On nettoie puis on recrée notre .jar
        run: mvn -B clean package --file pom.xml

      - name: Docker Build & Push # On crée et upload notre image Docker
        uses: docker/build-push-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
          repository: louisonsarlinmagnus/<your_micro_service>
          tag_with_ref: true
          tag_with_sha: true

```

:warning: **Il est très important de créer les secrets dans github** :warning:

### Sonar Cloud

Sonar Cloud permet d'analyser le code pour détecter des bugs et vulnérabilités.
Pour configurer SonarCLoud il faut créer un fichier `sonar-project.properties`.

Construction de `sonar-project.properties`:
```
sonar.organization=<replace with your SonarCloud organization key>
sonar.projectKey=<replace with the key generated when setting up the project on SonarCloud>
source.
```

Il faut aussi modifier notre github action en ajoutant le code suivant a la fin.

```yaml
  sonarcloud:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout master branch # On se place sur la branch master
      uses: actions/checkout@v2
    
    - name: SonarCloud Scan
      uses: sonarsource/sonarcloud-github-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

```
:warning: **il faudra créer les tokens `GITHUB_TOKEN` et `SONAR_TOKEN`** :warning:

Cet outil n'est pas bloquant même en cas de détection de vulnérabilité le depoyement se poursuivra. Il est donc **impératif** d'aller regarder sur la page sonarcloud les vulnérabilité détectées et de les corriger dans la version suivante.

A présent nous devons modifier le `pom.xml` en ajoutant
```xml
<properties>
  <sonar.projectKey>Projet-Spring-Boot_Integration</sonar.projectKey>
  <sonar.organization>projet-spring-boot-integration</sonar.organization>
  <sonar.host.url>https://sonarcloud.io</sonar.host.url>
</properties>
```


## CD: Déploient Continue

### Configuration AWS

Sur AWS: My Classrooms > Cloud Computing > Continue > Account details > Show.
Copier les information dans `%HOME%/.aws/credentials`
Puis ouvrir/créer `%HOME%/.aws/config` et coller:
```
[default]
region = us-east-1
output = yaml
```

Pour vérifier que vous avez bien accès au compte depuis le client: `aws ec2 describe-instances`.


