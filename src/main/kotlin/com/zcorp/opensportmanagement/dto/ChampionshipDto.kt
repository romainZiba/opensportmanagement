package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class ChampionshipDto(val name: String, @JsonIgnore val teamId: Int? = null, val _id: Int? = null)