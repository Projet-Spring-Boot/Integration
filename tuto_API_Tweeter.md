
## Tweeter API

This following part will explain the different steps to implement Tweeter API on your Spring Boot AP.

### Introduction
*Twitter is a popular micro-blogging and social networking service, enabling people to communicate with each other 140 characters at a time.*

The Spring Social Twitter project is an extension to Spring Social that enables integration with Twitter.


### How to get 

The following Maven dependency will add Spring Social Twitter to your project: 

```xml
<dependency>
  <groupId>org.springframework.social</groupId>
  <artifactId>spring-social-twitter</artifactId>
  <version>${org.springframework.social-twitter-version}</version>
</dependency>
```

### Configure Twitter Connectivity


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

Consumer keys and secrets may be different across environments (e.g., test, production, etc) it is recommended that these values be externalized.
As shown here, Spring 3.1's Environment is used to look up the application's consumer key and secret.

Optionally, you may also configure ConnectionFactoryRegistry and TwitterConnectionFactory in XML: 

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

### Tweeter API Binding

If you are using Spring Social's service provider framework, you can get an instance of Twitter from a Connection.
For example, the following snippet calls getApi() on a connection to retrieve a Twitter:

```java
Connection<Twitter> connection = connectionRepository.findPrimaryConnection(Twitter.class);
Twitter twitter = connection != null ? connection.getApi() : new TwitterTemplate();
```

Here, ConnectionRepository is being asked for the primary connection that the current user has with Twitter.
If connection to Twitter is found, a call to getApi() retrieves a Twitter instance that is configured with the connection details received when the connection was first established.
If there is no connection, a default instance of TwitterTemplate is created.

Once you have a Twitter, you can perform a several operations against Twitter. Twitter is defined as follows: 

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

### Tweeting

To post a message to Twitter :

```java
twitter.timelineOperations().updateStatus("Spring Social is awesome!");
```

### Reading Tweeter timelines

TimelineOperations also supports reading of tweets from one of the available Twitter timelines.

To retrieve the 20 most recent tweets from the public timeline:

```java
List<Tweet> tweets = twitter.timelineOperations().getPublicTimeline();
```

To retrieves the 20 most recent tweets from the user's home timeline:

```java
List<Tweet> tweets = twitter.timelineOperations().getHomeTimeline();
```
	

To retrieves the 20 most recent tweets from the user's friends timeline:

```java
List<Tweet> tweets = twitter.timelineOperations().getFriendsTimeline();
```


To get tweets from the authenticating user's own timeline:

```java
List<Tweet> tweets = twitter.timelineOperations().getUserTimeline();
```
		

To retrieve the 20 most recent tweets from a specific user's timeline (not necessarily the authenticating user's timeline), pass the user's screen name in as a parameter to getUserTimeline():

```java
List<Tweet> tweets = twitter.timelineOperations().getUserTimeline("USER_SCREENS_NAME");
```


To the four Twitter timelines, you may also want to get a list of tweets mentioning the user. The getMentions() method returns the 20 most recent tweets that mention the authenticating user:

```java
List<Tweet> tweets = twitter.timelineOperations().getMentions();
```

### Tweeter Private Messages

####List of Tweeter Friends

To get the friends list (in *friendsList*) :

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

#### Send and receved Messages

To send direct message use the *.sendDirectMessage(FRIEND_NAME, MESSAGE)* method:

```java
twitter.directMessageOperations().sendDirectMessage("UltroumVomitae", "If I had ten dollars...");
```

List of the 20 most recently received direct messages :

```java
List<DirectMessage> twitter.directMessageOperations().getDirectMessagesReceived();```