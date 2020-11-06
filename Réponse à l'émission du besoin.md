# PARTIE 1 :

- Utilisation de connections **Oauth** pour se connecter via des réseaux sociaux (ce qui implique que notre application doit être enregistrée dans ces réseaux afin de récupérer les secrets).
- Utilisation de l'[API][1] publiques des réseaux sociaux pour envoyer des  requètes `POST` (messages, flux vidéos ...). Par exemple utilisation de **twitter4j** pour poster des tweets.
- Création d'une page permettant à l'utilisateur de voir/modifier les informations de son compte sur notre application.
- Une première version a été développée par notre groupe, mais en utilisant [MySQL](https://www.mysql.com/). Il nous faut alors prendre en main [Redis](https://redis.io/) pour changer la base de donnée utilisée.

Ce que propose l'application : 

- Un nouvel utilisateur arrive sur notre application. Deux choix s'offrent à lui :

    1) Il créé un compte sur l'application de façon basique (nouvel enregistrement BDD).
    2) Il se connecte via un réseau social en Oauth et a la possibilité de créer un compte sur notre application via les données récupérées de son compte du réseau social.
</a>

- Lorsque l'utilisateur est connecté via un enregistrement **Oauth** (dans le cas de Twitter/Youtube/Facebook), on a la possibilité pour lui d'envoyer des requètes `POST`.



# PARTIE 2 :


[1]: interface de programmation d’application est un ensemble normalisé de classes, de méthodes, de fonctions et de constantes qui sert de façade par laquelle un logiciel offre des services à d'autres logiciels.