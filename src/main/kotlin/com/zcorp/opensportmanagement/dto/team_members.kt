package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zcorp.opensportmanagement.model.TeamMember

data class TeamMemberDto(
    val username: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<TeamMember.Role>,
    val licenseNumber: String,
    val email: String,
    val phoneNumber: String?,
    @JsonIgnore val teamId: Int,
    @JsonIgnore val confirmationId: String,
    val _id: Int
)

data class TeamMemberCreationDto(
    val firstName: String,
    val lastName: String,
    val roles: Set<TeamMember.Role>,
    val email: String,
    val phoneNumber: String?
)

data class TeamMemberUpdateDto(val licenseNumber: String)