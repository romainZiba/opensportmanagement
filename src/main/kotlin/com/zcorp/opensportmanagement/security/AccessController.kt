package com.zcorp.opensportmanagement.security

import com.zcorp.opensportmanagement.model.Role
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AccessController {

    fun getUserTeamIds(authentication: Authentication): List<Int> {
        return authentication.authorities.map { (it as OpenGrantedAuthority).teamId }
    }

    fun isUserAllowedToAccessTeam(authentication: Authentication, teamId: Int): Boolean {
        return getUserTeamIds(authentication).contains(teamId)
    }

    fun isTeamAdmin(authentication: Authentication, teamId: Int): Boolean {
        return authentication.authorities
                .map { (it as OpenGrantedAuthority).teamId to it.roles }
                .toMap()
                .getOrDefault(teamId, emptySet())
                .any { it == Role.ADMIN }
    }
}