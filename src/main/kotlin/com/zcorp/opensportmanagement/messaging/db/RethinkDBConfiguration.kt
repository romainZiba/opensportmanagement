package com.zcorp.opensportmanagement.messaging.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

import javax.annotation.PostConstruct

@Configuration
open class RethinkDBConfiguration {

    @Autowired
    private lateinit var env: Environment

    @PostConstruct
    fun init() {
        DBHOST = this.env.getProperty("rethinkdb.dbhost")
    }

    @Bean
    open fun connectionFactory(): RethinkDBConnectionFactory {
        return RethinkDBConnectionFactory(DBHOST)
    }

    @Bean
    internal open fun rethinkDbService(): RethinkDbService {
        return RethinkDbService()
    }

    companion object {
        var DBHOST = "127.0.0.1"
    }
}
