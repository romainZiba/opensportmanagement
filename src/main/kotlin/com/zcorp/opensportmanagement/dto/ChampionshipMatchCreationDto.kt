package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.Match
import java.time.LocalDate
import java.time.LocalTime

data class ChampionshipMatchCreationDto(
    val name: String,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val fromTime: LocalTime,
    val toTime: LocalTime,
    val placeId: Int,
    val matchType: Match.MatchType = Match.MatchType.CHAMPIONSHIP,
    val championshipId: Int,
    val opponentId: Int,
    val isTeamLocal: Boolean = true
)
