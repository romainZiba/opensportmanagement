package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zcorp.opensportmanagement.model.Season
import java.time.LocalDate

data class SeasonDto(val name: String,
                     val fromDate: LocalDate,
                     val toDate: LocalDate,
                     val status: Season.Status,
                     @JsonIgnore val teamId: Int? = null,
                     val _id: Int? = null)
