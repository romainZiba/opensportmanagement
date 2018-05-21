package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.Match
import java.time.LocalDateTime

data class MatchCreationDto(
    val name: String,
    val fromDate: LocalDateTime,
    val toDate: LocalDateTime? = null,
    val placeId: Int,
    val matchType: Match.MatchType,
    val championshipId: Int? = null,
    var opponentId: Int? = null,
    var isTeamLocal: Boolean? = true
)