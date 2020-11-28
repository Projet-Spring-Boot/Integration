# Mise en place de la CI CD

## Resources
Voici les fichiers en question:

- [build.yaml](/res/build.yaml)
- [Dockerfile](/res/Dockerfile)
- [sonar-project.properties](/res/sonar-project.properties)

## Description

### Introduction

Dans l'optique d'intégration et déployement perpétuel nous allons devoir mettre en place de nombreuses choses. Nous aurons besoin dans cette partie d'un compte [AWS](http://www.awseducate.com/), d'un compte [Docker](https://hub.docker.com/), d'accès au repository [Github](https://github.com/Projet-Spring-Boot/).

### Utilisation

Notre produit sera composé de 2 micro-services. Le µS Adminstration et le µS Interface. La team Intégration fournira a ces 2 micro-services le fichier `build.yaml` et le `Dockerfile`.
Ainsi a chaque push sur la branch `master` (ou main) une image Docker sera crée et push sur le hub docker. De la team Intégration récupérera les Docker images pour les envoyer dans AWS.

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
    branches: [ <your_branch> ]

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
 
      - name: Cache Maven packages
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2`

<<<<<<< HEAD
=======
      - name: Cache Maven packages
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2`

>>>>>>> dcf6f2bbcd4b3dadd616e0b846df0ddb6246b974
      - name: Maven Clean & Package
        run: mvn -B clean package --file pom.xml

      - name: Login
        uses: docker/login-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Build
        uses: docker/build-push-action@v2.2.0
        with:
          tags: louisonsarlinmagnus/integration:latest
          load: true

      - name: Push
        run: docker push louisonsarlinmagnus/integration:latest

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
   name: Analyse SonarCloud
   runs-on: ubuntu-latest
   steps:

   - name: Checkout master branch # On se place sur la branch master
     uses: actions/checkout@v2
   
   - name: SonarCloud Scan
     run: mvn -B verify sonar:sonar -Dsonar.projectKey=Projet-Spring-Boot_Integration -Dsonar.organization=projet-spring-boot-integration -Dsonar.host.url=https://sonarcloud.io/ -Dsonar.login=$SONAR_TOKEN
     env:
       GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
       SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

```
:warning: **il faudra créer les tokens `GITHUB_TOKEN` et `SONAR_TOKEN`** :warning:

Cet outil n'est pas bloquant même en cas de détection de vulnérabilité le depoyement se poursuivra. Il est donc **impératif** d'aller regarder sur la page sonarcloud les vulnérabilité détectées et de les corriger dans la version suivante.

## CD: Déploient Continue

### Configuration AWS CLI

Sur AWS: My Classrooms > Project Cloud > Continue > Account details > Show.
Copier les information dans `%HOME%/.aws/credentials`
Puis ouvrir/créer `%HOME%/.aws/config` et coller:
```
[default]
region = us-east-1
output = yaml
```

Pour vérifier que vous avez bien accès au compte depuis le client: `aws ec2 describe-instances`.

### Déploiement de ressource

Pour définir le déploiement des ressources sur AWS nous allons créer le fichier `cloudformation.yaml`:
```yaml
AWSTemplateFormatVersion: 2010-09-09
Description: 'CloudFormation Skeleton'
Metadata:
  Instances:
    Description: "The Metadata block is entirely optional."
Parameters: 
  InstanceTypeParameter: 
    Type: String
    Default: t2.micro
    AllowedValues: 
      - t2.micro
      - m5.large
    Description: Enter t2.micro or m5.large. Default is t2.micro.
Mappings: 
  RegionMap: 
    us-east-1: 
      "HVM64": "ami-00b882ac5193044e4"
    eu-west-1: 
      "HVM64": "ami-0f62aafc6efe8fd7b"
#Conditions: 
Resources: 
  EC2Instance: 
    Type: "AWS::EC2::Instance"
    Properties:
      ImageId: !FindInMap [RegionMap, !Ref "AWS::Region", HVM64]
      InstanceType: !Ref InstanceTypeParameter

Outputs:
  InstanceID:
    Description: "The Instance ID"
    Value: !Ref EC2Instance

```

Pour déployer l'instance dans AWS :`aws cloudformation deploy --template-file cloudformation.yaml --stack-name <your-login>-stack`.

Pour vérifier que vous avez bien accès à l'instance crée: `aws ec2 describe-instances`.

### ECS LAB

A présent on va déployer notre fichier de configuration pour créer notre cluster: `ecs-cluster.yaml`. Disponible [ici](/src/infra/ecs-cluster.yaml)