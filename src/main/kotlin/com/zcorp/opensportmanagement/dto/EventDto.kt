package com.zcorp.opensportmanagement.dto

import com.zcorp.opensportmanagement.model.Stadium
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EventDto(val name: String, val description: String, val fromDateTime: LocalDateTime?, val toDateTime: LocalDateTime?,
               val stadium: Stadium?, val place: String?, val reccurenceDays: MutableSet<DayOfWeek>?,
               val recurrenceFromDate: LocalDate?, val recurrenceToDate: LocalDate?, val recurrenceFromTime: LocalTime?,
               val recurrenceToTime: LocalTime?)