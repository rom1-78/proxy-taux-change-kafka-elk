---

# Proxy Taux de Change – Kafka + Elasticsearch + Kibana

## 🌍 **Contexte du Projet**

De nombreuses équipes internes de l’entreprise utilisent chacune un abonnement à une API de taux de change externe, ce qui génère des coûts importants.
Ce projet vise à **centraliser et mutualiser l’accès à ces données** via une application intermédiaire ("proxy") :

* Elle interroge l’API de taux de change externe (*exemple : [https://api.exchangerate-api.com/v4/latest/USD](https://api.exchangerate-api.com/v4/latest/USD)*)
* Elle publie les taux sur un topic Kafka en temps réel
* Elle indexe ces taux dans Elasticsearch
* Les données sont visualisables sur Kibana (tableaux de bord dynamiques)

Les autres équipes peuvent ainsi consommer les données depuis Kafka sans accès direct à l’API externe.

---

## 🚀 **Fonctionnalités principales**

* **Récupération des taux** : Appel automatisé à l’API externe (USD -> toutes devises)
* **Publication Kafka** : Envoi des taux au format JSON dans un topic Kafka
* **Consommation Kafka** : Un consumer Spring reçoit chaque taux en temps réel
* **Indexation Elasticsearch** : Stockage du JSON enrichi d’un timestamp dans Elasticsearch
* **Visualisation Kibana** : Création d’un dashboard avec tous les champs de chaque taux
* **API HTTP** : Endpoint `/produce` pour forcer un envoi manuel de données (utile pour test)

---

## 📦 **Arborescence du projet**

```
src/
├── main/
│   ├── java/com/learn/kafka/
│   │   ├── KafkaApplication.java
│   │   ├── consumer/         (Consumer Kafka)
│   │   ├── producer/         (Producer Kafka + Controller)
│   │   └── elasticsearch/    (Service d'indexation ES)
│   └── resources/
│       ├── application.properties
│       └── logback.xml
```

* fichiers classiques Maven (`pom.xml`)

---

## ⚙️ **Prérequis techniques**

* **Java 17+** (testé sur Java 23)
* **Spring Boot 3**
* **Docker** (pour lancer Kafka, Elasticsearch et Kibana facilement)
* **cURL** ou Postman pour tester l’API
* **Git**

---

## 🛠️ **Installation & Lancement**

### 1. **Cloner le projet**

```bash
git clone https://github.com/rom1-78/proxy-taux-change-kafka-elk.git
cd proxy-taux-change-kafka-elk
```

### 2. **Lancer Kafka, Elasticsearch et Kibana (via Docker)**

> Exemple de commande :

```bash
# Lancer Kafka + Zookeeper
docker-compose -f docker-compose-kafka.yml up -d

# Lancer Elasticsearch + Kibana
docker-compose -f docker-compose-elk.yml up -d
```

Vérifie que :

* [http://localhost:9092](http://localhost:9092) → Kafka OK
* [http://localhost:9200](http://localhost:9200) → Elasticsearch OK
* [http://localhost:5601](http://localhost:5601) → Kibana OK

### 3. **Configurer les propriétés Spring Boot**

Dans `src/main/resources/application.properties`, adapte si besoin :

```properties
kafka.topic.name=kafka-kata-TR
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=tp-kafka-step1
elasticsearch.url=http://localhost:9200
```

### 4. **Lancer l’application Java**

```bash
./mvnw spring-boot:run
# ou
mvn spring-boot:run
```

---

## 👨‍💻 **Utilisation & Démo**

### **Envoi manuel (test) via HTTP**

Pour forcer un envoi de taux de change :

```bash
curl -X POST "http://localhost:8080/produce?content={JSON}"
# Exemple :
curl -G --data-urlencode "content={\"base\":\"USD\",\"rates\":{\"EUR\":0.92,\"GBP\":0.8}}" http://localhost:8080/produce
```

### **Automatisation (Scheduler)**

L’application appelle régulièrement l’API externe, publie le JSON sur Kafka, et indexe automatiquement dans Elasticsearch (avec un champ `timestamp` ajouté).

---

### **Visualisation dans Kibana**

1. Aller sur [http://localhost:5601](http://localhost:5601)
2. Menu latéral → Discover
3. Sélectionner l’index `exchange-rates`
4. Filtrer (si besoin) pour afficher uniquement les documents contenant un timestamp
5. Créer un dashboard pour explorer les taux de change dans le temps, par devise, etc.

---

## 📝 **Organisation du code**

* `KafkaApplication.java` : point d’entrée Spring Boot
* `producer/` :

  * `MessageProducer.java` : producteur Kafka
  * `ProducerController.java` : endpoint `/produce`
* `consumer/` :

  * `MessageConsumer.java` : consomme le topic Kafka et déclenche l’indexation ES
* `elasticsearch/` :

  * `CurrencyRepository.java` : service pour indexer le JSON + timestamp dans ES
* `scheduler/` (si présent) : permet de scheduler les appels automatiques à l’API de taux de change

---

## 📸 **Exemples de logs**

```
Message envoyé sur Kafka : {"base":"USD","rates":{"EUR":0.92,"GBP":0.8}}
Message reçu par le Consumer : {...}
Document indexé dans Elasticsearch avec timestamp : 2024-06-06T14:37:22.130Z
```

---

## 🧹 **Limitation actuelle**

* Les anciens documents sans `timestamp` peuvent encore exister (filtre possible dans Kibana).
* L’index Elasticsearch est créé dynamiquement : tu peux personnaliser son mapping si besoin pour forcer certains types (float, date, etc).

---

## 👤 **Auteur**

* [romain fretet](https://github.com/rom1-78)

---

## 📹 **Démo vidéo**

* [Lien de la video]([https://teams.microsoft.com/l/meetingrecap?driveId=b%21QtvLuYD26keJMy-OHu4WqnvoNhtwGW5Ao3RpkO4gaelzm4Jf9CnbRoTCuk4V2J1o&driveItemId=01VZ6VELBC4APYHEXLDFBLWFLUV6FIKNGF&sitePath=https%3A%2F%2Fensup-my.sharepoint.com%2F%3Av%3A%2Fg%2Fpersonal%2Fromain_fretet_ensitech_eu%2FESLgH4OS6xlCuxV0r4qFNMUBSkD4nTK7TnyrQtTJ0KGEsw&fileUrl=https%3A%2F%2Fensup-my.sharepoint.com%2Fpersonal%2Fromain_fretet_ensitech_eu%2FDocuments%2FEnregistrements%2FR%25C3%25A9union%2520avec%2520Romain%2520FRETET-20250608_195719-Enregistrement%2520de%2520la%2520r%25C3%25A9union.mp4%3Fweb%3D1&threadId=19%3Ameeting_ZjBiZjFlZDEtMzk5Mi00ZTg2LTk1NDctMWZlN2Y1NWU1MzMy%40thread.v2&organizerId=8733381d-cb88-4a87-9f10-3f0ee85a2b9d&tenantId=9840a2a0-6ae1-4688-b03d-d2ec291be0f9&callId=4136a3d2-d079-4db0-9284-4b6399bb18af&threadType=Meeting&meetingType=MeetNow&subType=RecapSharingLink_RecapCore](https://ensup-my.sharepoint.com/:v:/g/personal/romain_fretet_ensitech_eu/ESLgH4OS6xlCuxV0r4qFNMUBkHdHjyzTNzyOQfSVTcV-3A?e=gtTaj4))

---

## 📄 **Licence**

Ce projet est fourni dans le cadre du TP Big Data / ENSITECH, usage pédagogique uniquement.

---
