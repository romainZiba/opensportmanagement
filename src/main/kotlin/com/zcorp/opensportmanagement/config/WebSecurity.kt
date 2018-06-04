package com.zcorp.opensportmanagement.config

import com.zcorp.opensportmanagement.security.JWTAuthenticationFilter
import com.zcorp.opensportmanagement.security.JWTAuthorizationFilter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
open class WebSecurity(
    @Qualifier("osm_user_details") private val userDetailsService: UserDetailsService,
    private val properties: OsmProperties
) : WebSecurityConfigurerAdapter() {

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.PUT, "/accounts/confirmation").permitAll()
                .antMatchers(HttpMethod.GET, "/docs").permitAll()
                // TODO: for authenticated users only
                .antMatchers("/messagesWS/**").permitAll()
                .and().authorizeRequests().anyRequest()
                .authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager()))
                .addFilter(JWTAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.csrf().disable()
        http.headers().frameOptions().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder())
    }

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = properties.allowedOrigins
        configuration.allowedMethods = listOf("POST", "PUT", "GET", "OPTIONS", "DELETE")
        configuration.allowedHeaders = listOf("Authorization", "Content-Type")
        configuration.allowCredentials = true
        configuration.maxAge = 3600
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}