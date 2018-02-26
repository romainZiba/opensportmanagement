package com.zcorp.opensportmanagement.dto

data class UserDto(val firstName: String, val lastName: String, val username: String, val email: String, val phoneNumber: String?,
                   val teamsDto: Set<TeamDto>)