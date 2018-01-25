package com.zcorp.opensportmanagement.security

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AccessController {

    fun getUserTeamIds(authentication: Authentication): List<Int> {
        return authentication.authorities.map { it.authority.toInt() }
    }

    fun isUserAllowedToAccessTeam(authentication: Authentication, teamId: Int): Boolean {
        return getUserTeamIds(authentication).contains(teamId)
    }
}