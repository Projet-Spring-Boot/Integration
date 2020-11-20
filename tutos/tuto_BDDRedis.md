## BDD Redis

### Les dépendances

Premièrement il faut ajouter les dépendances dans le ficheir XML:

```xml
<!-- REDIS -->

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

<!-- REDIS -->
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
    
    // Getteurs - Setteurs...
}
```

>*TIPS! Les setteurs et les getteurs sont les mêmes que pour MySql
>(la base de donnée que nous avont étudié pendant 2 et demis en cours)*

Création du Repository

```java
@Repository
public interface StudentRepository extends CrudRepository<Student, String> {}
```

### Accès à la base

Création d'un nouvel objet Student :

```java
Student student = new Student(
	  "Eng2015001", "John Doe", Student.Gender.MALE, 1);
	studentRepository.save(student);
}
```

Récuperer un objet par ID : 

```java
Student retrievedStudent = studentRepository.findById("Eng2015001").get();
```

Modifier les données d'un objet :

```java
retrievedStudent.setName("Richard Watson");
studentRepository.save(student);
```

Supprimer un objet :

```java
studentRepository.deleteById(student.getId());
```

Récupérer tout les objets d'une "table" :

```java
// --- Création des Edutiants à placer dans notre table --- //
Student engStudent = new Student(
"Eng2015001", "John Doe", Student.Gender.MALE, 1);
Student medStudent = new Student(
  "Med2015001", "Gareth Houston", Student.Gender.MALE, 2);
studentRepository.save(engStudent);
studentRepository.save(medStudent);

// -- Récupération de touts les éléments de la table -- //
List<Student> students = new ArrayList<>();
studentRepository.findAll().forEach(students::add);
```

>Bravo ! Tout compte faits Redis c'est pas si mal
>
>**Grâce à ce tuto Redis ne fais pas un plis !**