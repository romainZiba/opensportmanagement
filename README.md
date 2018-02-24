# OpenSportManagement
Open source project for managing a sport team. This project uses SpringBoot framework with Kotlin.

## Get ready 

### PostgreSql Docker

If you want to use postgresql instead of in-memory database, uncomment the related part in the application.yml. Then, you can use a docker like this:

```
docker run --name opensportmanagement-psql -p 5432:5432 -e POSTGRES_DB=open -e POSTGRES_PASSWORD=open -d postgres
```

### RethinkDB

This project uses RethinkDB to provide real-time communication
Visit https://www.rethinkdb.com/docs/install/ for more information on how to install it.
For now, I did not manage to configure the port number. It seems that there is a problem with the RethinkDB java driver and Kotlin. This imposes to use port 8080 (the default one).

### Try it out

Either launch the Application.kt which is a @SpringBootApplication or try ./gradlew bootRun

## REST api

Documentation coming soon