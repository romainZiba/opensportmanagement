package com.zcorp.opensportmanagement.dto

import java.time.LocalDateTime


class MatchDto(val name: String, val description: String, val fromDateTime: LocalDateTime, val toDateTime: LocalDateTime,
               val stadiumName: String, val opponentName: String)