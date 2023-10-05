# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Docker Compose Support](https://docs.spring.io/spring-boot/docs/3.1.4/reference/htmlsingle/index.html#features.docker-compose)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.1.4/reference/htmlsingle/index.html#actuator)
* [Spring Shell](https://spring.io/projects/spring-shell)
* [Postgis](https://postgis.net/)

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* postgres: [`postgis/postgis:16-3.4`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.

### Running the Application

#### Prereqs
* Docker running
* JDK 17

This application utilizes the PostgresDB with Postgis extention.  You will need to connect to a working instance to run the app.

There is a main class called `GeicoApplication.java` that can be used to run the application.

Note that the normal `gradle bootRun` doesn't work properly with Spring Shell.  You could build with `gradle jar` and then run the jar executable `java -jar geico-0.0.1-SNAPSHOT.jar`.  This jar will be in the `build/lib` directory.

For the data, when Postgres starts it will create a few tables and populate them with some GeoLocations of sample Assistants.

### My thoughts
* I started looking at K Nearest Neighbor (kNN) algorithm as that seemed to be the biggest chunk of this work.
* Instead of implementing myself, PostGis is a great solution that can already scale to the needs, so pivoted to learning Postgis.
* Looked at some alternatives such as H2 embedded with gis as an option, but stayed with Postgis
* Found an interesting article about [performance](https://www.alibabacloud.com/blog/postgresql-nearest-neighbor-query-performance-on-billions-of-geolocation-records_597015) for kNN on Postgis that confirms to me it will scale
* I wanted to play around with a command line wrapper and went with Spring Shell for the interface
* Reached for my standby JPA for persisting.  I did have to bring in a special library for spatial work with JPA
* Lastly I have used the technique to leverage a transaction table with locking to solve the concurrency issues.  Sadly while it looks right, I did run out of time to fully get the transaction boundaries fully complete.
* I decided to add a small statemachine as well.  Even though there are just two states in this (available, assigned), I can see it expanding as the business grows.
* Additionally, while Customer takes a back seat in this exercise, I could also see adding things like "service level" to the customer and "performance measures" to the Assistant.  This could be used to not only find closest, but also 5-star service gets preferred to higher paid customers.
* I wanted to utilize Testcontainers to do some more functional tests, but also ran out of time to implement