# OpenSportManagement
Open source project for managing a sport team. This project uses SpringBoot framework with Kotlin.

## Get ready 
### How to get your development environment up and running?
The future of the web is HTTPS. In order to use it, these are the steps to generate easily a self-signed certificate:

`keytool -genkey -alias opensportmanagement -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`

Then, put your configuration in the application.yml file.

### Try it out
`./gradlew bootRun`

### To build the Jar
`./gradlew bootJar`

### PostgreSQL and docker
This project uses postgresql by default. You can use a Postgres docker like this:

```
docker run --name opensportmanagement-psql -p 5432:5432 -e POSTGRES_DB=open -e POSTGRES_PASSWORD=open -d postgres
```

## REST api
The REST api is built thanks to Spring REST Docs and is contained in the jar.

## Configuration of the application

For now, all configuration is done via the application.yml. Future features will enable to make some configuration for
each team.

### Events subscriptions

Your team members can subscribe to events only before a number of days, configured by:
- opensportmanagement.events.daysBefore

### Notifications

Notifications are sent by mail to the team members that have not responded yet. The configuration includes:
- spring.mail to configure the sender
- opensportmanagement.notifications.enabled to enable/disable notifications
- opensportmanagement.notifications.daysBefore to express how many days before an event you want the members to receive
 the notification

## RethinkDB
This project uses RethinkDB to provide real-time communication. It is possible not to use it but the real-time messaging
 features won't work.

Visit https://www.rethinkdb.com/docs/install/ for more information on how to install it.

## Known issues
Usage of @SpringBootTest and @AutoConfigureMockMvc instead of @WebMvcTest because the @EnableWebSecurity is currently
not included: refer to https://github.com/spring-projects/spring-boot/issues/6514

Spring Rest docs and OpenAPI: https://github.com/spring-projects/spring-restdocs/issues/213

With RethinkDB, port 8080 (the default one) is the only possible port. The configuration of the driver does not work