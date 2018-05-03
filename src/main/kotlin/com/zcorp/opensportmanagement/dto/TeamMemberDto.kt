package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.TeamMember

data class TeamMemberDto(val username: String,
                         val firstName: String,
                         val lastName: String,
                         val roles: Set<TeamMember.Role>,
                         val teamId: Int)