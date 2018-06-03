package com.zcorp.opensportmanagement.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.ArrayList
import javax.naming.AuthenticationException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

    init {
        authenticationManager = authManager
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse?
    ): Authentication {
        try {
            val creds = ObjectMapper().readValue(req.inputStream, com.zcorp.opensportmanagement.model.Account::class.java)

            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            creds.username,
                            creds.password,
                            ArrayList<GrantedAuthority>())
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain,
        auth: Authentication
    ) {
        res.addCookie(JWTUtils.getAccessCookie(auth))
        val cookie = Cookie(INSECURE_COOKIE_KEY, "")
        cookie.maxAge = EXPIRATION_TIME
        res.addCookie(cookie)
    }
}