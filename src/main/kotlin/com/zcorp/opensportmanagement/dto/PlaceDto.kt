package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zcorp.opensportmanagement.model.Place

data class PlaceDto(
    val name: String,
    val address: String,
    val city: String,
    val type: Place.PlaceType,
    @JsonIgnore val teamId: Int? = null,
    val _id: Int? = null
)