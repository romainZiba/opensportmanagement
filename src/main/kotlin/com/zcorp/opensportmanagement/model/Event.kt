package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

@MappedSuperclass
abstract class Event() {

    var name: String = ""
    var description: String = ""
    var recurrent: Boolean = false
    var fromDateTime: LocalDateTime? = null
    var toDateTime: LocalDateTime? = null
    @ElementCollection(fetch = FetchType.EAGER) var reccurenceDays: MutableSet<DayOfWeek> = mutableSetOf()
    var recurrenceFromDate: LocalDate? = null
    var recurrenceToDate: LocalDate? = null
    var recurrenceFromTime: LocalTime? = null
    var recurrenceToTime: LocalTime? = null
    @ManyToOne var stadium: Stadium? = null
    var place: String? = null
    @ManyToOne @JsonBackReference var team: Team? = null
    @Id @GeneratedValue var id: Int = -1

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, stadium: Stadium,
                team: Team) : this() {
        this.name = name
        this.description = description
        this.recurrent = false
        this.fromDateTime = fromDateTime
        this.toDateTime = toDateTime
        this.stadium = stadium
        this.team = team
    }

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, place: String,
                team: Team) : this() {
        this.name = name
        this.description = description
        this.fromDateTime = fromDateTime
        this.toDateTime = toDateTime
        this.place = place
        this.team = team
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                team: Team) : this() {
        this.name = name
        this.description = description
        this.reccurenceDays = reccurenceDays
        this.recurrenceFromDate = recurrenceFromDate
        this.recurrenceToDate = recurrenceToDate
        this.recurrenceFromTime = recurrenceFromTime
        this.recurrenceToTime = recurrenceToTime
        this.stadium = stadium
        this.team = team
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                team: Team) : this() {
        this.name = name
        this.description = description
        this.reccurenceDays = reccurenceDays
        this.recurrenceFromDate = recurrenceFromDate
        this.recurrenceToDate = recurrenceToDate
        this.recurrenceFromTime = recurrenceFromTime
        this.recurrenceToTime = recurrenceToTime
        this.place = place
        this.team = team
    }

    override fun toString(): String {
        return "Event(name='$name', description='$description', recurrent=$recurrent, fromDateTime=$fromDateTime, toDateTime=$toDateTime, reccurenceDays=$reccurenceDays, recurrenceFromDate=$recurrenceFromDate, recurrenceToDate=$recurrenceToDate, recurrenceFromTime=$recurrenceFromTime, recurrenceToTime=$recurrenceToTime, stadium=$stadium, place=$place, team=$team, id=$id)"
    }
}

const val championship = "CHAMPIONSHIP"
const val friendly = "FRIENDLY"
const val between_us = "BETWEEN_US"
const val tournament = "TOURNAMENT"
const val training = "TRAINING"
const val other = "OTHER"


enum class EventType(val type: String) {
    CHAMPIONSHIP(championship),
    FRIENDLY(friendly),
    BETWEEN_US(between_us),
    TOURNAMENT(tournament),
    TRAINING(training),
    OTHER(other)
}