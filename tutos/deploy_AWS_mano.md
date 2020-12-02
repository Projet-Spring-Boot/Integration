
## Déployer son application sur AWS manuelement 

Deployement manuel #sauvontlesmeubles

### Créer une instance depuis l'interface AWS en ligne 

Se rendre sur la page "créer une Instance" ici :

https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#LaunchInstanceWizard:

- Selectionner **Image Ubuntu Server 18.04**
- Selectionner *t2.micro*
- Prochaine étape : Garder les paramètres par défaut
- Prochaine étape : Garder les paramètres par défaut
- Prochaine étape : Garder les paramètres par défaut
- Selectionner *All traffic* au lieu de *TCP*
- Lancer

Ensuite il faudra sélectionner "Create a new key pair" (dans la fenêtre pop-up) et enter un nom.
**Puis télécharger**

Maintenant lancer l'instance.

### Traiter les clés paires

Téléchargez PuttyGen ici : https://www.ssh.com/ssh/putty/download

Dans PuttyGen cliquer sur *Load* puis sélectionner la clé téléchargée plus tôt.
Puis enregistrer la clé extraire en cliquant sur *Save Private key*

### Se connecter à notre Instance 

Sur AWS cliquer sur l'instance > Action > Se Connecter à l'instance > Client SSH 

ici l'adresse de connection est de la forme : *ubuntu@ec2-34-201-161-142.compute-1.amazonaws.com*

*(Essaie pas de t'y connecter j'ai détruit l'instance petit filou que tu es)*

Lancer Putty.

- Host Name : Notre nom d'instance récupéré plus haut (*ubuntu@ec2-34-201-161-142.compute-1.amazonaws.com*)
- Port : 22
- Menu > SSH > Auth : Private Key > Browse... : Indiquer le chemin de la clé générée plus haut (*.ppk*)

Bravo ! Vous avez désormais un terminal sur votre instance. La sorcellerie noire.

### Installer une base sur notre Instance

liste des commandes :

```bash
sudo apt-get update
```

```bash
sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
```

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

```bash
sudo apt-get install docker-ce docker-ce-cli containerd.io
```

```bash
apt-cache madison docker-ce
```

```bash
sudo apt-get install docker-ce docker-ce-cli containerd.io
```

```bash
sudo apt install docker.io
```

```bash
sudo apt install docker-compose
```

```bash
sudo apt install maven
```

```bash
sudo apt install mysql-server
```

```bash
sudo sed -Ei 's/^(bind-address|log)/#&/' /etc/mysql/my.cnf
```

Arrêter mysql (libérer le port 3306)

```bash
sudo systemctl stop mysql
```

### Copier notre projet sur l'Instance

Télécharger File Zilla : https://filezilla-project.org/

Aller dans édition > Paramètres > SFTP > Ajouter un fichier de clé (donnez le chemin de la cle *.ppk*)

Ensuite retourner chercher l'adresse de l'Instance (*ubuntu@ec2-34-201-161-142.compute-1.amazonaws.com*)
et placez dans l'entrée *"Hôte"* (Port 22)

Cliquez sur Quikconnect.

Maintenant que la connection est établie vous pouvez Drag and Drop votre projet ou vous le désirez dans l'arborescence de l'Instance.

Notre projet est désormais sur notre Instance !

Pour construire notre projet ne pas oublier les commandes :

```bash
mvn clean
mvn package
```

### Construire l'Image Docker sur notre Instance 

Dans l'instance aller dans le dossier du projet puis entrer la commande suivante :

```bash
sudo docker build -t <NomConteneur> .
```

*Rappel* Récupérer la liste des images :

```bash
sudo docker images
```

Le conteneur est créé. Pour le lancer (mapé sur le port 8080 de notre Instance) :

```bash
sudo docker run -p 8080:8080 <NomConteneur>
```

Notre application est donc à l'adresse IPv4 externe de l'Instance au port 8080.

Tout fonctionne bravo, bonne nuit.

### Notes 

Si ERROR : pas de manifester de l'attribut, dans "app.jar"

Dans xml :

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Deployer plusieurs images Docker 

Il faut créer un nouveau fichier *docker-compose.yml* à la racine du projet.

Exemple : 

```yaml
version: '3'

services:
  docker-mysql:
    image: mysql:5.7
    environment:
      - MYSQL_ROOT_PASSWORD=motdepasse
      - MYSQL_DATABASE=mysql
    ports:
      - 3307:3306

  app:
    image: imageadmin
    ports:
       - 8080:8080
    depends_on:
       - docker-mysql
```

Ici notre réseau sera composé de *monimage* en local et mysql directement sur le repo Docker mysql.
*Pour Redis remplacer mysql par redis et le port est 6379 et supprimer les variables d'environnement*

Modifier le fichier src/main/ressources/application.properties

```yaml
...

# ===============================
# DATABASE
# ===============================

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://docker-mysql:3306/mysql
spring.datasource.username=root
spring.datasource.password=motdepasse

# ===============================
# JPA / HIBERNATE
# ===============================

...
```

**Ligne à modifier** : *spring.datasource.url=jdbc:mysql://[NOM DOCKER SQL]:[PORT BDD SQL]/[NOM BDD SQL]*

Lancer un réseau de Docker :

```bash
sudo docker-compose up
```

Stopper le réseau de Docker :

```bash
sudo docker-compose down
```

*NOTES*

Pour commenter les host name sur fichier mysql 

```bash
sudo sed -Ei 's/^(bind-address|log)/#&/' /etc/mysql/my.cnf
```

Arrêter mysql (libérer le port 3306)

```bash
sudo systemctl stop mysql
```

**C'était le CIJDD**
