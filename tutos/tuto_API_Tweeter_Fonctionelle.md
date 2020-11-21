
## Tweeter API Le retour Tweeter4J

Cette partie explique les différentes étapes pour implémenter l'API Tweeter sur votre appli SpringBoot.

**Elle est une méthode aternative Fonctionelle de la librairie Spring-Tweeter**

### Introduction
Twitter est un service de micro-blogging et de réseautage social populaire, permettant aux gens de communiquer entre eux, par message de 140 caractères.
Le projet "Spring Social Twitter" est une extension de Spring Social qui permet l'intégration avec Twitter.

### Récuperer les Tockens Access (pour que notre API soie identifiée par Tweeter)

Pour récupérer les accès à l'API Tweeter il faut formuler une demande ici : https://developer.twitter.com/en/apps

Une fois la réponse emise par Tweeter, renseigner les Tokens dans le fichier *social-cfg.properties*:

```xml
# Twitter

social.twitter-consumer-key=""
social.twitter-consumer-secret=""
```

Ensuite dans *"Persmission Settings"* selectionner *Read and Write*.

Activer *Enable 3-legged OAuth*.

Le Call Back URLs est : *http://localhost:8080/auth/twitter*

Le Web Site sera celui de l'isen : *https://isen.fr/*

### Comment Implementer 

La dépendance Maven suivante ajoutera Tweeter4j au projet:

```xml
<dependency>
    <groupId>org.twitter4j</groupId>
    <artifactId>twitter4j-core</artifactId>
    <version>4.0.7</version>
</dependency>
```

Pour récupérer ces valeures dans notre code, on utilise une classe de configuration
(celles-ci sont appelées par défaut par Spring au début) : 

```java
@Configuration
@PropertySources({ @PropertySource("classpath:social-cfg.properties") })
@ConfigurationProperties(prefix = "social")
public class SocialProperties {

    private String twitterConsumerKey;
    private String twitterConsumerSecret;

    ...

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }
    public void setTwitterConsumerKey(String twitterConsumerKey) {
        this.twitterConsumerKey = twitterConsumerKey;
    }
    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }
    public void setTwitterConsumerSecret(String twitterConsumerSecret) {
        this.twitterConsumerSecret = twitterConsumerSecret;
    }



}
```

Pour utiliser cette valeur dans le code, on injecte un object de type SocialProperties :

```java
@Service
public class YourServiceImpl implements YourService {

    private SocialProperties socialProperties;

    ...

    @Autowired
    public void setSocialProperties(SocialProperties socialProperties) {
        this.socialProperties = socialProperties;
    }

    // Use property 'social.twitter-consumer-key'
    @Override
    public void foo() {

        String bar = socialProperties.getTwitterConsumerKey();
    }
}
```

### Configurer la conectivité de Twitter


```java
public class JavaTweet {

	static String consumerKeyStr = "XXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	static String consumerSecretStr = "XXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	static String accessTokenStr = "XXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	static String accessTokenSecretStr = "XXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	public static void main(String[] args) {

		try {
			Twitter twitter = new TwitterFactory().getInstance();

			twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
			AccessToken accessToken = new AccessToken(accessTokenStr,
					accessTokenSecretStr);

			twitter.setOAuthAccessToken(accessToken);

			// exploitation de l'objet tweeter pour tweeter ou récupérer la timeline

		} catch (TwitterException te) {
			te.printStackTrace();
		}
	}

}
```

Consumer keys et secrets peuvent être différents selon les environnements (par exemple,
test, production, etc.), il est recommandé d'externaliser ces valeurs.

### Tweeter

Pour poster un Tweet sur la timeline de l'utilisateur (poster) :

*tweeter était l'objet crée plus haut*

```java
twitter.updateStatus("MESSAGE");
```

### Lire des timelines Tweeter

Pour récupérer les 20 tweets les plus récents de la timeline de l'utilisateur:

```java
ResponseList<Status> timeline = twitter.getHomeTimeline() ;
```

*Retour : liste de Tweets*

Pour exploiter chaque Tweet de la liste :

```java
timeline.forEach(s -> {     //On parcours la liste des 20 Tweets
    s.getUser().getName();      // Récupère le nom de l'utilisateur qui a tweeté
    s.getText();                // Récupère le texte du tweet
    MediaEntity[] medias = s.getMediaEntities();    // Récupère la liste des médias associés au Tweet
    System.out.println("media :");
    for(int i=0 ; i<medias.length ; i++) {
        medias[i].getText();        // Récupère le lien du tweet
        medias[i].getMediaURL();    // Récupère l'url vers un média
    }

    if(s.isRetweet()) {     //Si ce tweet est un retweet
        Status status = s.getRetweetedStatus();
        status.getText();               // Récupère le texte du tweet retweeté
        status.getUser().getName();     //Récupère le nom de l'utilisateur qui a tweeté le tweet retweeté (vous suivez?)
        MediaEntity[] medias2 = status.getMediaEntities();
        for(int i=0 ; i<medias2.length ; i++) {
            medias2[i].getText();       // Récupère le lien du tweet
            medias2[i].getMediaURL();   // Récupère la l'url vers un média
        }
}
```

### Messages Privés Tweeter

#### Envoyer et recevoir des Messages

Pour envoyer des messages privés :

```java
twitter.sendDirectMessage("@NAME", "MESSAGE");
```

*Mon @ est @_doolmen, envoi moi de douces paroles !*

Pour avoir la liste des "count" derniers messages privés recus (count:20 par défaut et 50 max):

```java
// --- PAS ENCORE FONCTIONEL --- //

Response<List> DirectsMessages = twitter.getDirectMessages(count);

DirectsMessages.forEach(message ->{
    message.senderScreeName();  // Récupère le nom de l'envoyeur
    message.getText();          // Récupère le texte du message
});
```

**Bravo vous pouvez désormais utiliser Tweeter depuis votre API GLHF !**
