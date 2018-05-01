package com.zcorp.opensportmanagement.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import java.net.URLEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.http.Cookie

class JWTUtils {
    companion object {

        fun getAccessCookie(auth: Authentication): Cookie {
            val newToken = JWTUtils.getToken(auth)
            val cookie = Cookie(COOKIE_KEY, URLEncoder.encode(TOKEN_PREFIX + newToken, URL_ENCODING))
            cookie.isHttpOnly = true
            cookie.secure = false
            cookie.maxAge = EXPIRATION_TIME
            return cookie
        }

        private fun getToken(auth: Authentication): String {
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

        fun refreshRequired(token: String): Boolean {
            val issuedAt = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .body
                    .issuedAt
            val tokenAge = ChronoUnit.HOURS.between(issuedAt.toInstant(), Instant.now())
            if (tokenAge > REFRESH_TOKEN_RATE) {
                return true
            }
            return false
        }

        private fun generateToken(claims: Map<String, String>, subject: String): String {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME * 1000))
                    .setIssuedAt(Date())
                    .signWith(SignatureAlgorithm.HS512, SECRET)
                    .compact()
        }
    }
}