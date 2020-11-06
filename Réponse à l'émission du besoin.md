# PARTIE 1 :

- Utilisation de connections **Oauth** pour se connecter via des réseaux sociaux (ce qui implique que notre application doit être enregistrée dans ces réseaux afin de récupérer les secrets).
- Utilisation de l'[API](https://fr.wikipedia.org/wiki/Interface_de_programmation) publique des réseaux sociaux pour envoyer des requêtes `POST` (messages, flux vidéos ...). Par exemple utilisation de **twitter4j** pour poster des tweets.
- Création d'une page permettant à l'utilisateur de voir/modifier les informations de son compte sur notre application.
- Une première version a été développée par notre groupe, mais en utilisant [MySQL](https://www.mysql.com/). Il nous faut alors prendre en main [Redis](https://redis.io/) pour changer la base de donnée utilisée.

Ce que propose l'application : 

- Un nouvel utilisateur arrive sur notre application. Deux choix s'offrent à lui :

    + Il créé un compte sur l'application de façon basique (nouvel enregistrement BDD).  
    **OU**  
    + Il se connecte via un réseau social en **Oauth** et a la possibilité de créer un compte sur notre application via les données récupéréees de son compte du réseau social.  


- Lorsque l'utilisateur est connecté via un enregistrement **Oauth** (dans le cas de Twitter/Youtube/Facebook), il a la possibilité d'envoyer des messages sur ces réseaux sociaux via des requêtes `POST`.



# PARTIE 2 :
Les groupes de travail sont répartis comme suit:

1. Sécurité + Optimisation de la BDD
2. Interface Graphhique + Optimisation expérience utilisateur
3. Gestion utilisateurs + reset MDP + Analyse requêtes
</a>

**Nous partons du principe que les groupes 2 et 3 sont deux microservices différents.**

Le *microservice 3* se chargera de la gestion des utilisateurs de l'application. Cette gestion se fera via des repositories/services, qui manipuleront des objets propres aux utilisateurs.

Le *microservice 2* se chargera de l'interface. Elle devra posséder la même définition des objets utilisateurs que le *microservice 3*; afin de récupérer les données utilisateurs via des requêtes `GET` envoyées vers le *microservice 3*.

Pour la connection, le *microservice 2* enverra une requête `POST` au *microservice 3*.

L'interface devra permettre à l'utilisateur de se connecter avec son compte à l'application.
Elle devra aussi proposer à un nouvel utilisateur de s'enregistrer sur l'application, soit via un réseau social, soit en créant un compte. Ainsi, l'interface doit connaître les deux `POST` différents à envoyer dans les 2 cas possibles (si l'utilisateur se connecte via un réseau social, un paramètre "signInProvider" est présent dans le body de la requête.)

Une documentation détaillée sur l'intégration de nouveaux réseaux sera bientôt disponible. (en spposant que nous utilisons une framework de service provider)
