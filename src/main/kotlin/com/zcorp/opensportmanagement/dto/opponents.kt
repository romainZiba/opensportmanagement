package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class OpponentDto(
    val name: String,
    val phoneNumber: String,
    val email: String,
    val imgUrl: String,
    @JsonIgnore val teamId: Int?,
    val _id: Int
)

data class OpponentCreationDto(
    val name: String,
    val phoneNumber: String = "",
    val email: String = ""
)