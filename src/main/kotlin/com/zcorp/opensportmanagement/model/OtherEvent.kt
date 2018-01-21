package com.zcorp.opensportmanagement.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "event")
class OtherEvent : Event {

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                stadium: Stadium, team: Team) :
            super(name, description, fromDateTime, toDateTime, stadium, team)

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                place: String, team: Team) :
            super(name, description, fromDateTime, toDateTime, place, team)

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                team: Team) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, stadium, team)

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                team: Team) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, place, team)

    override fun toString(): String {
        return "OtherEvent() ${super.toString()}"
    }


}