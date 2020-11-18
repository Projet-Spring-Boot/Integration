
## Tweeter API

Cette partie explique les différentes étapes pour implémenter l'API Tweeter sur votre appli SpringBoot.

### Introduction
Twitter est un service de micro-blogging et de réseautage social populaire, permettant aux gens de communiquer entre eux, par message de 140 caractères.
Le projet "Spring Social Twitter" est une extension de Spring Social qui permet l'intégration avec Twitter.

### Récuperer les Tockens Access (pour que notre API soie identifiée par Tweeter)

Pour récupérer les accès à l'API Tweeter il faut formuler une demande ici : https://developer.twitter.com/en/apps

Une fois la réponse emise par Tweeter, renseigner les Tokens dans le fichier *social-cfg.properties*:

```xml
# Twitter
# http://localhost:8080/auth/twitter

twitter.consumer.key=
twitter.consumer.secret=
# twitter.scope=       
```

Ensuite dans *"Persmission Settings"* selectionner *Read and Write*.

Activer *Enable 3-legged OAuth*.

Le Call Back URLs est : *http://localhost:8080/auth/twitter*

Le Web Site sera celui de l'isen : *https://isen.fr/*

### Comment Implementer 

La dépendance Maven suivante ajoutera Spring Social Twitter au projet:

```xml
<dependency>
  <groupId>org.springframework.social</groupId>
  <artifactId>spring-social-twitter</artifactId>
  <version>${org.springframework.social-twitter-version}</version>
</dependency>
```

### Configurer la conectivité de Twitter


```java
@Configuration
public class SocialConfig {

    @Inject
    private Environment environment;
	
    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new TwitterConnectionFactory(
            environment.getProperty("twitter.consumerKey"),
            environment.getProperty("twitter.consumerSecret")));
        return registry;
    }

}
```

Consumer keys et secrets peuvent être différents selon les environnements (par exemple, test, production, etc.), il est recommandé d'externaliser ces valeurs.
L'environnement de Spring 3.1 est utilisé pour rechercher la clé et le secret du consommateur de l'application.

Optionnellement, il faudra configurer ConnectionFactoryRegistry et TwitterConnectionFactory dans le fichier pom.XML: 

```xml
<bean id="connectionFactoryLocator" class="org.springframework.social.connect.support.ConnectionFactoryRegistry">
    <property name="connectionFactories">
        <list>
            <bean class="org.springframework.social.twitter.connect.TwitterConnectionFactory">
                <constructor-arg value="${twitter.consumerKey}" />
                <constructor-arg value="${twitter.consumerSecret}" />				
            </bean>
        </list>
    </property>
</bean>
```

### Liaison API - Tweeter

Si vous utilisez le cadre de fournisseur de services de Spring Social, vous pouvez obtenir une instance de Twitter à partir d'une connexion.
Par exemple, l'extrait de code suivant appelle la methode *.getApi()* sur une connexion pour récupérer un Twitter (*Token Access*):

```java
Connection<Twitter> connection = connectionRepository.findPrimaryConnection(Twitter.class);
Twitter twitter = connection != null ? connection.getApi() : new TwitterTemplate(CONSUMER_KEY, CONSUMER_SECRET);
```

Ici, ConnectionRepository indique la connexion principale de l'utilisateur actuel avec Twitter.
Si une connexion à Twitter est trouvée, un appel à getApi () récupère une instance Twitter qui est configurée avec les détails de connexion reçus lors de l'établissement de la connexion.
S'il n'y a pas de connexion, une instance par défaut de TwitterTemplate est créée.

Une fois que vous avez un Twitter, vous pouvez effectuer plusieurs opérations sur Twitter :

```java
public interface Twitter {

   boolean isAuthorizedForUser();

   DirectMessageOperations directMessageOperations();

   FriendOperations friendOperations();

   GeoOperations geoOperations();

   ListOperations listOperations();

   SearchOperations searchOperations();

   TimelineOperations timelineOperations();

   UserOperations userOperations();

}
```

### Tweeter

Pour poster un Tweet sur la timeline de l'utilisateur (poster) :

```java
twitter.timelineOperations().updateStatus("Spring Social is awesome!");
```

### Lire des timelines Tweeter

TimelineOperations prend également en charge la lecture de tweets à partir des différentes timelines Twitter disponibles.

Pour récupérer les 20 tweets les plus récents de la timeline publique:

```java
List<Tweet> tweets = twitter.timelineOperations().getPublicTimeline();
```

Pour récupérer les 20 tweets les plus récents de la timeline de l'utilisateur:

```java
List<Tweet> tweets = twitter.timelineOperations().getHomeTimeline();
```
	

Pour récupérer les 20 tweets les plus récents de la timeline d'un ami de l'utilisateur:

```java
List<Tweet> tweets = twitter.timelineOperations().getFriendsTimeline();
```

Pour récupérer les tweets de l'utilisateur authentifié:

```java
List<Tweet> tweets = twitter.timelineOperations().getUserTimeline();
```

Pour récupérer les 20 tweets les plus récents de la chronologie d'un utilisateur spécifique (pas nécessairement suivant la chronologie de l'utilisateur authentifié), il faut transmettre le nom de l'utilisateur en tant que paramètre à getUserTimeline ():
```java
List<Tweet> tweets = twitter.timelineOperations().getUserTimeline("USER_SCREENS_NAME");
```

Pour les quatre chronologies Twitter, vous pouvez également obtenir une liste de tweets mentionnant l'utilisateur.
La méthode *.getMentions()* renvoie les 20 tweets les plus récents qui mentionnent l'utilisateur authentifié:
```java
List<Tweet> tweets = twitter.timelineOperations().getMentions();
```

### Messages Privés Tweeter

#### Liste des amis Tweeter

Pour avoir la liste de tous les amis de l'utilisateur connecté (dans *friendsList*) :

```java
List<org.springframework.social.twitter.api.TwitterProfile> friendsList;
CursoredList<Long> friendIdList;
long[] userIdArray;

friendIdList =  twitterTemplate.friendOperations().getFriendIds();
userIdArray = new long[friendIdList.size()];
for(int i=0; i<friendIdList.size(); i++)
    userIdArray[i] = friendIdList.get(i);
friendsList = twitterTemplate.userOperations().getUsers(userIdArray);
```

#### Envoyer et recevoir des Messages

Pour envoyer des messages privés utiliser la méthode *.sendDirectMessage(FRIEND_NAME, MESSAGE)*:

```java
twitter.directMessageOperations().sendDirectMessage("UltroumVomitae", "If I had ten dollars...");
```

Pour avoir la liste des 20 derniers messages privés recus :

```java
List<DirectMessage> twitter.directMessageOperations().getDirectMessagesReceived();
```


