package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.Season
import java.time.LocalDate

data class SeasonDto(val name: String, val fromDate: LocalDate, val toDate: LocalDate, val status: Season.Status)
