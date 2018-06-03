package com.zcorp.opensportmanagement.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.net.URLDecoder
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

    private val LOG = LoggerFactory.getLogger(JWTAuthorizationFilter::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain
    ) {
        val authCookie = req.cookies?.find { cookie -> cookie.name == ACCESS_TOKEN_COOKIE_KEY }
        val insecureCookie = req.cookies?.find { cookie -> cookie.name == INSECURE_COOKIE_KEY }

        if (insecureCookie == null || authCookie == null) {
            chain.doFilter(req, res)
            return
        }
        val decodedToken = URLDecoder.decode(authCookie.value, URL_ENCODING)
        if (!decodedToken.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res)
            return
        }
        val authentication = getAuthentication(req)
        if (authentication == null) {
            chain.doFilter(req, res)
            return
        }

        if (JWTUtils.refreshRequired(decodedToken)) {
            res.addCookie(JWTUtils.getAccessCookie(authentication))
        }
        // Account is authorized
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, res)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.cookies.find { cookie -> cookie.name == "access_token" }?.value
        if (token != null) {
            // parse the token.
            val decodedToken = URLDecoder.decode(token, URL_ENCODING)
            try {
                val user = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(decodedToken.replace(TOKEN_PREFIX, ""))
                        .body
                        .subject
                val stringAuthorities = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(decodedToken.replace(TOKEN_PREFIX, ""))
                        .body[AUTHORITIES] as String

                val mapper = jacksonObjectMapper()
                mapper.findAndRegisterModules()
                val authorities = mapper.readValue<List<OpenGrantedAuthority>>(stringAuthorities)
                return if (user != null) {
                    UsernamePasswordAuthenticationToken(user, null, authorities)
                } else null
            } catch (e: ExpiredJwtException) {
                LOG.error("Token expired ", e)
            }
        }
        return null
    }
}