
## Facebook API

Cette partie explique les différentes étapes pour implémenter l'API Facebook sur votre appli SpringBoot.

### Introduction

FAcebook est un service de micro-blogging et de réseautage social populaire, permettant aux gens de communiquer entre eux par des postes pouvant inclure des liens, images ou vidéos.
Le projet "Spring Social Facebook" est une extension de Spring Social qui permet l'intégration avec Facebook.

### Récuperer les Tockens Access (pour que notre API soie identifiée par Facebook)

Pour récupérer les accès à l'API Facebook il faut formuler une demande ici : <A DETERMINER>

Une fois la réponse emise par Facebook, renseigner les Tokens dans le fichier *social-cfg.properties*:

```xml
# Facebook
# http://localhost:8080/auth/facebook
  
facebook.app.id=1084911261562762
facebook.app.secret=81a324fdbc4cade1ee25523c7bff58b3
facebook.scope=public_profile,email
```

### Comment Implementer 

La dépendance Maven suivante ajoutera Spring Social Facebook au projet:

```xml
<dependency>
  <groupId>org.springframework.social</groupId>
  <artifactId>spring-social-facebook</artifactId>
  <version>${org.springframework.social-facebook-version}</version>
</dependency>
```

### Configurer la conectivitée de Facebook


```java
@Configuration
public class ConnectionFactoryConfig {
	
    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(facebookClientId, facebookClientSecret));
        return registry;
    }

    @Value("${facebook.clientId}")
    private String facebookClientId;
	
    @Value("${facebook.clientSecret}")
    private String facebookClientSecret;
	
}
```

Ici, la conection avec Facebook est enregistrée avec ConnectionFactoryRegistry via la méthode *.addConnectionFactory()*.
Étant donné que les clés et les secrets des consommateurs peuvent être différents d'un environnement à l'autre (par exemple, test, production, etc.), il est recommandé d'externaliser ces valeurs.
Par conséquent, ils sont câblés avec @Value en tant que valeurs d'espace réservé de propriété à résoudre par la prise en charge des espaces réservés de propriété de Spring.

Optionelement, il faudra configurer ConnectionFactoryRegistry et FacebookConnectionFactory dans le fichier pom.XML: 

```xml
<bean id="connectionFactoryLocator" class="org.springframework.social.connect.support.ConnectionFactoryRegistry">
    <property name="connectionFactories">
        <list>
            <bean class="org.springframework.social.facebook.connect.FacebookConnectionFactory">
                <constructor-arg value="${facebook.clientId}" />
                <constructor-arg value="${facebook.clientSecret}" />				
            </bean>
        </list>
    </property>
</bean>
```

### Liaison API - Facebook

Si vous utilisez le cadre de fournisseur de services de Spring Social, vous pouvez obtenir une instance de Facebook à partir d'une connexion.
Par exemple, l'extrait de code suivant appelle la methode *.getApi()* sur une connexion pour récupérer un Facebook (*Token Access*):

```java
Connection<Facebook> connection = connectionRepository.findPrimaryConnectionToApi(Facebook.class);
Facebook facebook = connection.getApi();
```

Ici, ConnectionRepository indique la connexion principale de l'utilisateur actuel avec Facebook.
Si une connexion à Facebook est trouvée, un appel à getApi () récupère une instance Facebook qui est configurée avec les détails de connexion reçus lors de l'établissement de la connexion.
S'il n'y a pas de connexion, une instance par défaut de FacebookTemplate est créée.

Une fois que vous avez un Facebook, vous pouvez effectuer plusieurs opérations :

```java
public interface Facebook extends GraphApi {

    CommentOperations commentOperations();

    EventOperations eventOperations();
	
    FeedOperations feedOperations();

    FriendOperations friendOperations();
	
    GroupOperations groupOperations();

    LikeOperations likeOperations();
	
    MediaOperations mediaOperations();
	
    PlacesOperations placesOperations();
	
    UserOperations userOperations();
	
}
```

### Poster un Message

Pour poster un Message Textuel sur le mur de l'utilisateur:

```java
facebook.feedOperations().updateStatus("I'm trying out Spring Social!");
```

Pour poster un Message Textuel avec lien sur le mur de l'utilisateur:

```java
FacebookLink link = new FacebookLink("http://www.springsource.org/spring-social", 
        "Spring Social", 
        "The Spring Social Project", 
        "Spring Social is an extension to Spring to enable applications to connect with service providers.");
facebook.feedOperations().updateStatus("I'm trying out Spring Social!", link);
```

### Récupérer le Feed

Si vous souhaitez lire les messages du feed d'un utilisateur, FeedOperations a le choix entre plusieurs méthodes.
La méthode *.getFeed()* récupère les publications récentes sur le mur d'un utilisateur.
Lorsqu'il est appelé sans paramètre, il récupère les messages du mur de l'utilisateur qui s'authentifie:

```java
List<Post> feed = facebook.feedOperations().getFeed();
```

Récupérer le feed d'un utilisateur en particulier :

```java
List<Post> feed = facebook.feedOperations().getFeed("USER_NAME");
```

*L'envoi de messages privés, depuis l'arrivée de Messenger, n'est plus possible depuis cette API*