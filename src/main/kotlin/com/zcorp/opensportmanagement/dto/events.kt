package com.zcorp.opensportmanagement.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class EventDto(val _id: Int,
                    val name: String,
                    val fromDateTime: LocalDateTime,
                    val toDateTime: LocalDateTime?,
                    val placeId: Int,
                    val presentMembers: List<TeamMemberDto>,
                    val absentMembers: List<TeamMemberDto>,
                    val waitingMembers: List<TeamMemberDto>,
                    @JsonIgnore val teamId: Int? = null,
                    var localTeamName: String? = null,
                    var visitorTeamName: String? = null,
                    var localTeamImgUrl: String? = null,
                    var visitorTeamImgUrl: String? = null,
                    var visitorTeamScore: Int? = null,
                    var localTeamScore: Int? = null,
                    var isDone: Boolean? = null)

data class EventCreationDto(val name: String,
                            val fromDate: LocalDate,
                            val toDate: LocalDate,
                            var fromTime: LocalTime,
                            val toTime: LocalTime,
                            val placeId: Int,
                            val isRecurrent: Boolean = false,
                            var recurrenceDays: MutableSet<DayOfWeek> = mutableSetOf())