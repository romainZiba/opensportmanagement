package com.zcorp.opensportmanagement.config

import com.zcorp.opensportmanagement.security.JWTAuthenticationFilter
import com.zcorp.opensportmanagement.security.JWTLoginFilter
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.session.SessionManagementFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
open//@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
class SecurityConfig : WebSecurityConfigurerAdapter() {

    private val SIGN_UP_URL: String = "/users/sign-up"

    override fun configure(http: HttpSecurity) {
        //http.cors().disable()
        http.cors()
                .and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(CorsFilter(), SessionManagementFilter::class.java)
                .addFilterBefore(JWTLoginFilter("/users/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
}
