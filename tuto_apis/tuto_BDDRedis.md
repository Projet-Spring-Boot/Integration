
## BDD Redis

### Les dépendances

Premièrement il faut ajouter les dépendances dans le ficheir XML:

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
	<artifactId>spring-data-redis</artifactId>
	<version>2.3.3.RELEASE</version>
</dependency>
	 
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.3.0</version>
    <type>jar</type>
</dependency>
```

>*Facon alternattive :*
>```xml
><dependency>
>    <groupId>org.springframework.boot</groupId>
>    <artifactId>spring-boot-starter-data-redis</artifactId>
>    <version>2.3.3.RELEASE</version>
></dependency>
>```

### Configuration de Redis

Configuration du *Bean* :

```java
@Bean
JedisConnectionFactory jedisConnectionFactory() {
    return new JedisConnectionFactory();
}
 
@Bean
public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    return template;
}
```
Ici la *connection-related properties* est manquante. C'est alors celle par défaut qui est utilisée.

Pour une configuration personalisée :

```java
@Bean
JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory jedisConFactory
      = new JedisConnectionFactory();
    jedisConFactory.setHostName("localhost");
    jedisConFactory.setPort(6379);
    return jedisConFactory;
}
```

>*Ici les valeures entrée sont les même que par défaut*

### Repertoire Redis

>*Pour l'exemple l'entitée Student sera crée*

```java
@RedisHash("Student")
public class Student implements Serializable {
  
    public enum Gender { 
        MALE, FEMALE
    }
 
    private String id;
    private String name;
    private Gender gender;
    private int grade;
    // ...
}
```

Création du Repository

```java
@Repository
public interface StudentRepository extends CrudRepository<Student, String> {}
```

>*suite du tuto : https://www.baeldung.com/spring-data-redis-tutorial*
