package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.AbstractEvent
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class EventDto(
    val _id: Int,
    val name: String,
    val fromDateTime: LocalDateTime,
    val toDateTime: LocalDateTime?,
    val placeId: Int,
    val placeName: String,
    val presentMembers: List<TeamMemberDto>,
    val absentMembers: List<TeamMemberDto>,
    val waitingMembers: List<TeamMemberDto>,
    val openForRegistration: Boolean,
    val cancelled: Boolean,
    val teamId: Int,
    var localTeamName: String? = null,
    var visitorTeamName: String? = null,
    var localTeamImgUrl: String? = null,
    var visitorTeamImgUrl: String? = null,
    var visitorTeamScore: Int? = null,
    var localTeamScore: Int? = null,
    var isDone: Boolean? = null
)

data class EventCreationDto(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val fromTime: LocalTime,
    val toTime: LocalTime,
    val placeId: Int,
    val type: AbstractEvent.EventType,
    val name: String? = null,
    val isRecurrent: Boolean = false,
    val recurrenceDays: MutableSet<DayOfWeek> = mutableSetOf()
)

data class EventModificationDto(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val fromTime: LocalTime,
    val toTime: LocalTime,
    val placeId: Int
)