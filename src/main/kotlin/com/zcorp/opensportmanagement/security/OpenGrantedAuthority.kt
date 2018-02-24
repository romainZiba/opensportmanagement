package com.zcorp.opensportmanagement.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.zcorp.opensportmanagement.model.Role
import org.springframework.security.core.GrantedAuthority

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenGrantedAuthority(val teamId: Int, val roles: Set<Role>) : GrantedAuthority {

    override fun getAuthority(): String {
        val sb = StringBuilder()
        sb.append(teamId).append(SEPARATOR)
        roles.forEach { sb.append(it).append(ROLE_SEPARATOR) }
        return sb.toString()
    }

    companion object {
        val ROLE_SEPARATOR = ";"
        val SEPARATOR = ":"

        fun toAuthority(s: String): OpenGrantedAuthority {
            val split = s.split(SEPARATOR)
            val roles = split[1].split(ROLE_SEPARATOR)
            return OpenGrantedAuthority(split[0].toInt(), roles.map { Role.valueOf(it) }.toSet())
        }
    }
}