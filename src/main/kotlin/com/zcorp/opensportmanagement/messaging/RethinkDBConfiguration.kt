package com.zcorp.opensportmanagement.messaging

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import javax.annotation.PostConstruct

@Configuration
open class RethinkDBConfiguration {

    @Autowired
    private lateinit var env: Environment

    private var dbHost = "127.0.0.1"

    @PostConstruct
    fun init() {
        dbHost = this.env.getProperty("rethinkdb.dbhost") ?: "127.0.0.1"
    }

    @Bean
    open fun connectionFactory(): RethinkDBConnectionFactory {
        return RethinkDBConnectionFactory(dbHost)
    }
}
