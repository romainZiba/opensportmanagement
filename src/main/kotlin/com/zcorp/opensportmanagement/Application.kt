package com.zcorp.opensportmanagement

import com.zcorp.opensportmanagement.config.EventsProperties
import com.zcorp.opensportmanagement.config.NotificationsProperties
import com.zcorp.opensportmanagement.config.OsmProperties
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(OsmProperties::class, NotificationsProperties::class, EventsProperties::class)
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
