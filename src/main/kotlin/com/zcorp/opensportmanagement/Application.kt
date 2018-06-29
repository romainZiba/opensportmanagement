package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.config.EventsProperties
import com.zcorp.opensportmanagement.config.NotificationsProperties
import com.zcorp.opensportmanagement.config.OsmProperties
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.service.AccountService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(OsmProperties::class, NotificationsProperties::class, EventsProperties::class)
open class Application {
    @Bean
    @Transactional
    open fun init(
        accountService: AccountService
    ) = CommandLineRunner {
        if (accountService.getAccountsCount() == 0L) {
            val admin = Account(firstName = "admin",
                    lastName = "admin",
                    password = "password",
                    email = "",
                    phoneNumber = "",
                    temporary = false,
                    username = "admin",
                    globalAdmin = true)
            accountService.createAccount(admin)
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
