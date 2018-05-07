package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore

data class StadiumDto(val name: String,
                      val address: String,
                      val city: String,
                      @JsonIgnore val teamId: Int? = null,
                      val _id: Int? = null)