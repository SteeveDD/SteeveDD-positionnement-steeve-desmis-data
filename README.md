# Projet d'Analyse de Ventes avec Docker

Ce projet met en œuvre une architecture à deux services conteneurisés avec Docker : un service pour l'exécution de scripts Java d'import et d'analyse de données, et un service pour le stockage des données dans une base de données SQLite.

## Prérequis

* Docker installé sur votre système.

## Initialisation et Lancement du Projet

* Ouvrir cmd

0.  **Cloner le dépôt (si ce n'est pas déjà fait) :**
    ```
    git clone https://github.com/SteeveDD/SteeveDD-positionnement-steeve-desmis-data.git
    ```
1.  **Accéder au fichier Docker et Docker-Compose :**
    ```
    cd SteeveDD-positionnement-steeve-desmis-data
    cd analyse-ventes
    ```

2.  **Construire les images et démarrer les conteneurs (lancez docker au préalable et fermez tout les projets utilisant le port : 8080):**
    ```
    docker-compose up --build -d
    ```
    Cette commande construit les images Docker définies dans le fichier `docker-compose.yml` (si nécessaire) et lance les conteneurs en arrière-plan.

3.  **Lancer le script :**
    ```
    docker-compose up java-app
    ```
    
## Interagir avec la Base de Données SQLite en SQL

Pour interagir avec la base de données SQLite, vous pouvez exécuter l'interpréteur de commandes SQLite à l'intérieur du conteneur `sqlite_service`.


1.  **Accéder au shell du conteneur SQLite :**
 
    ```
    docker-compose exec sqlite-db sh
    ```

2.  **Lancer l'interpréteur SQLite et ouvrir la base de données :**
    Une fois à l'intérieur du conteneur, exécutez :
    ```
    sqlite3 /data/database.db
    ```
    Vous serez alors dans l'invite de commandes SQLite, où vous pourrez exécuter des requêtes SQL directement sur la base de données. Par exemple :

    ```sql
    SELECT * FROM ventes LIMIT 10;
    ```
    Pour quitter l'interpréteur SQLite, tapez `.exit`. Pour quitter le shell du conteneur, tapez `exit`.

## Autres Commandes Utiles

* **Arrêter tous les services :**
    ```
    docker-compose down
    ```
* **Afficher l'état des services :**
    ```
    docker-compose ps
    ```

## Livrables

Les livrables de ce projet incluent :

* Le schéma de l’architecture conçue.
* Le schéma des données (MCD, MLD).
* Le `Dockerfile`.
* Le fichier `docker-compose.yml`.
* Les scripts d'exécution Java.
* Le fichier `requetes.sql`.
* Une note rappelant les résultats d’analyse.