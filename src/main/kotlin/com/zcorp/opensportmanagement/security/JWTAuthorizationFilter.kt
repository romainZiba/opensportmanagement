package com.zcorp.opensportmanagement.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList


class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

    private val LOG = LoggerFactory.getLogger(JWTAuthorizationFilter::class.java)


    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(req: HttpServletRequest,
                                  res: HttpServletResponse,
                                  chain: FilterChain) {
        val header = req.getHeader(HEADER_STRING)

        if (header == null || !header!!.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res)
            return
        }

        val authentication = getAuthentication(req)

        if (authentication == null) {
            chain.doFilter(req, res)
            return
        }

        // User is authorized. Refresh the access token
        val token = Jwts.builder()
                .setSubject((authentication.principal.toString()))
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact()
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token)

        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(req, res)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(HEADER_STRING)
        if (token != null) {
            // parse the token.
            try {
                val user = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token!!.replace(TOKEN_PREFIX, ""))
                        .body
                        .subject
                var teamNames: MutableList<String> = Jwts.parser()
                        .setSigningKey(SECRET)
                        .parseClaimsJws(token!!.replace(TOKEN_PREFIX, ""))
                        .body[TEAM_NAMES] as MutableList<String>
                return if (user != null) {
                    UsernamePasswordAuthenticationToken(user, null, teamNames.mapTo(ArrayList<GrantedAuthority>()) { SimpleGrantedAuthority(it) })
                } else null
            } catch (e: ExpiredJwtException) {
                LOG.error("Token expired ", e)
            }

        }
        return null
    }
}