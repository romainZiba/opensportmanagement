package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class ChampionshipDto(val name: String, @JsonIgnore val teamId: Int, val _id: Int)
data class ChampionshipCreationDto(val name: String)