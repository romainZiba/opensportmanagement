package com.zcorp.opensportmanagement.config

import org.h2.server.web.WebServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class WebConfiguration {
    @Bean
    internal open fun h2servletRegistration(): ServletRegistrationBean {
        val registrationBean = ServletRegistrationBean(WebServlet())
        registrationBean.addUrlMappings("/console/*")
        return registrationBean
    }
}