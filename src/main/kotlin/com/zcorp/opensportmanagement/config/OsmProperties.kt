package com.zcorp.opensportmanagement.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("opensportmanagement")
data class OsmProperties(var allowedOrigins: List<String> = listOf())