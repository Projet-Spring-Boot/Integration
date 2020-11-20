Afin de simplifier le travail d'intégration entre micro-services, merci de suivre ces contraintes dans votre code :

# 1. Framework de requêtes HTTP

Pour envoyer des requêtes au service d'administration, je conseil d'utiliser **WebClient** car c'est un framework très moderne.  
WebClient est un **client HTTP** moderne et alternatif au RestTemplate développé par Spring. Non seulement il fournit une API synchrone traditionnelle, mais il prend également en charge une approche asynchrone et non bloquante efficace.  

Voir: [WebClient](https://www.baeldung.com/spring-5-webclient)

Cependant, vous pouvez aussi utiliser :

- *HttpComponents* d'Apache
- *OkHttp* de Square
- *RestTemplate* de Spring
- *Jetty* ...


# 2. Utilisation des fichiers de propriétés

## 2.1. Pour les endpoints

Afin de requêter un autre service, vous aurez besoin de connaître le *HOST*, le *PORT* de se service une fois déployé, et la ressource vers laquelle taper.  

Exemple : Requête *GET* vers `http://168.212.226.204:8080/users`.  
Ici, il est important de savoir que l'endpoint */users* va retourner par exemple la liste des utilisateurs. Il s'agit du chemin vers la ressource.  

**Ainsi, pour faciliter la configuration, nous demandons aux groupes (surtout ADMINISTRATION) de ne pas hard coder leur chemin vers les ressources, mais de les écrire dans un fichier `.properties`.**  

Exemple : 

Fichier `endpoints.properties` sous `/src/main/resources` :

```bash
users.path=/users
users.by.id=/users/{id}
```

Controller correspondant :

```java
@RestController
@PropertySources({ @PropertySource("classpath:endpoints.properties") })
public class YourController {

    ...

    @RequestMapping(value = "${users.path}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectUserRole getUsers() {
        return foo();
    }

    @RequestMapping(value = "${users.by.id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectUser getUserById(@PathVariable(name = "id") int id) {
        return bar();
    }

}
```

Cela permet de modifier seulement le fichier de propriété sans toucher au code pour se phaser plus simplement entre micro-services.

## 2.2. Pour la configuration des URL à requêter

De la même façon, il faut que le *HOST* du service à requêter ne soit **pas hardcodé**. Il faut donc le mettre dans un fichier de propriété.
Cependant, on voudrait pouvoir passer quelque part en paramètre le *HOST* d'un autre micro-service une fois connu.
Le fichier de propriété contiendra donc une variable d'environnement qu'on pourra setter lors de la compilation du code.

Exemple (point de vu du groupe IHM) :

Fichier `host.properties` sous `src/main/resources` :

```bash
server.administration-host= ${ADMINISTRATION_HOST}
```

Pour récupérer cette valeur dans notre code, on utilise une classe de configuration (celles-ci sont appelées par défaut par Spring au début) :
Plus d'informations [ici](https://www.baeldung.com/configuration-properties-in-spring-boot).

```java
@Configuration
@PropertySources({ @PropertySource("classpath:host.properties") })
@ConfigurationProperties(prefix = "server")
public class HostProperties {


    private String administrationHost;

    ...

    public String getAdministrationHost() {
        return administrationHost;
    }

    public void setAdministrationHost(String administrationHost) {
        this.administrationHost = administrationHost;
    }
}
```

Pour utiliser cette valeur dans le code, on injecte un object de type HostProperties :

```java
@Service
public class YourServiceImpl implements YourService {

    private HostProperties hostProperties;

    ...

    @Autowired
    public void setHostProperties(HostProperties hostProperties) {
        this.hostProperties = hostProperties;
    }

    // Use property 'administration-host'
    @Override
    public void userAdministrationHostValue() {
        
        foo(hostProperties.getAdministrationHost());
    }
}
```

Ainsi, plutôt que de compiler le projet en utilisant par exemple `mvn package`, on utilisera `ADMINISTRATION_HOST=http://168.212.226.204:8080 mvn package`.


Une fois cela fait, on pourra mettre à jour les URL vers les ressources simplement depuis un fichier de propriété, et le *HOST* sur lequel taper sera configuré à la fin lors de la compilation.
