# OpenSportManagement
Open source project for managing a sport team. This project uses SpringBoot framework with Kotlin.

## Get ready 

### How to get your development environment up and running?

The future of the web is HTTPS. In order to use it, these are the steps to generate easily a self-signed certificate:

`keytool -genkey -alias opensportmanagement -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`

Then, put your configuration in the application.yml file.

### PostgreSql Docker

If you want to use postgresql instead of in-memory database, uncomment the related part in the application.yml. Then, you can use a docker like this:

```
docker run --name opensportmanagement-psql -p 5432:5432 -e POSTGRES_DB=open -e POSTGRES_PASSWORD=open -d postgres
```

### RethinkDB

This project uses RethinkDB to provide real-time communication
Visit https://www.rethinkdb.com/docs/install/ for more information on how to install it.
For now:

1) Port 8080 (the default one) is the only possible port
2) A rethinkdb instance is a must have

### Try it out

`./gradlew bootRun`

## REST api

Documentation coming soon

## Known issues

Usage of @SpringBootTest and @AutoConfigureMockMvc instead of @WebMvcTest because the @EnableWebSecurity is currently
not included: refer to https://github.com/spring-projects/spring-boot/issues/6514