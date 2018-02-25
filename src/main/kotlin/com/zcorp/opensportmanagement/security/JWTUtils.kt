package com.zcorp.opensportmanagement.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import java.util.*

class JWTUtils {
    companion object {
        fun getToken(auth: Authentication): String {
            val claims: MutableMap<String, String> = mutableMapOf()
            val mapper = jacksonObjectMapper()
            mapper.findAndRegisterModules()
            val authoritiesAsString = mapper.writeValueAsString(auth.authorities)
            claims[AUTHORITIES] = authoritiesAsString
            var username = ""
            if (auth.principal is User) {
                username = (auth.principal as User).username
            } else if (auth.principal is String) {
                username = auth.principal as String
            }
            return generateToken(claims, username)
        }

        private fun generateToken(claims: Map<String, String>, subject: String): String {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS512, SECRET)
                    .compact()
        }
    }
}