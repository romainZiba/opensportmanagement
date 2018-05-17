package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.Match
import java.time.LocalDateTime


data class MatchDto(val _id: Int, val name: String, val description: String, val fromDateTime: LocalDateTime, val toDateTime: LocalDateTime,
                    val stadiumName: String, val opponentName: String)

data class MatchCreationDto(val name: String,
                            val fromDate: LocalDateTime,
                            val toDate: LocalDateTime? = null,
                            val placeId: Int,
                            val matchType: Match.MatchType,
                            val championshipId: Int? = null,
                            var opponentId: Int? = null,
                            var isTeamLocal: Boolean? = true)