package com.zcorp.opensportmanagement.security

import com.zcorp.opensportmanagement.model.Role
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AccessController(val teamMemberRepository: TeamMemberRepository) {

    fun getUserTeamIds(authentication: Authentication): List<Int> {
        return authentication.authorities.map { it.authority.toInt() }
    }

    fun isUserAllowedToAccessTeam(authentication: Authentication, teamId: Int): Boolean {
        return getUserTeamIds(authentication).contains(teamId)
    }

    fun isTeamAdmin(authentication: Authentication, teamId: Int): Boolean {
        val teamMember = teamMemberRepository.findByUsername(authentication.name, teamId)
        return teamMember?.roles?.contains(Role.COACH) ?: false
    }
}