package com.zcorp.opensportmanagement.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("opensportmanagement")
data class OsmProperties(var allowedOrigins: List<String> = listOf())

@ConfigurationProperties("opensportmanagement.notifications")
data class NotificationsProperties(
    var enabled: Boolean = false,
    var daysBefore: Long = 7
)