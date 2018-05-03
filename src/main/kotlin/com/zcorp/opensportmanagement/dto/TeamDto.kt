package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.Team

data class TeamDto(val _id: Int, val name: String, val sport: Team.Sport, val genderKind: Team.Gender, val ageGroup: Team.AgeGroup)
