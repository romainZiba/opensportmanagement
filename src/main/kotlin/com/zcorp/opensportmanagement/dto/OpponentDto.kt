package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class OpponentDto(
    val name: String,
    val phoneNumber: String,
    val email: String,
    val imgUrl: String = "",
    @JsonIgnore val teamId: Int? = null,
    val _id: Int? = null
)