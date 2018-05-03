package com.zcorp.opensportmanagement.dto

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class EventDto(val _id: Int,
                    val name: String,
                    val description: String,
                    val fromDate: LocalDateTime?,
                    val toDate: LocalDateTime?,
                    val place: String?,
                    val stadiumId: Int?,
                    val presentMembers: List<String>,
                    val absentMembers: List<String>,
                    var localTeamName: String? = null,
                    var visitorTeamName: String? = null,
                    var localTeamImgUrl: String? = null,
                    var visitorTeamImgUrl: String? = null,
                    var visitorTeamScore: Int? = null,
                    var localTeamScore: Int? = null,
                    var isDone: Boolean? = null,
                    var reccurenceDays: MutableSet<DayOfWeek>? = null,
                    var recurrenceFromTime: LocalTime? = null,
                    val recurrenceToTime: LocalTime? = null,
                    var recurrenceFromDate: LocalDate? = null,
                    val recurrenceToDate: LocalDate? = null)