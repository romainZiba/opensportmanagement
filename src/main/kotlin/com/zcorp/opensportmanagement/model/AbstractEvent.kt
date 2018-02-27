package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

@MappedSuperclass
abstract class AbstractEvent() {

    var name: String = ""
    var description: String = ""
    var recurrent: Boolean = false
    var fromDateTime: LocalDateTime? = null
    var toDateTime: LocalDateTime? = null
    @ElementCollection
    var reccurenceDays: MutableSet<DayOfWeek> = mutableSetOf()
    var recurrenceFromDate: LocalDate? = null
    var recurrenceToDate: LocalDate? = null
    var recurrenceFromTime: LocalTime? = null
    var recurrenceToTime: LocalTime? = null

    @ManyToOne
    var stadium: Stadium? = null

    var place: String? = null

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference
    var team: Team? = null

    @GeneratedValue
    @Id
    var id: Int = -1

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
        return "AbstractEvent(name='$name', description='$description', id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractEvent

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    enum class EventType(val type: String) {
        CHAMPIONSHIP(championship),
        FRIENDLY(friendly),
        BETWEEN_US(between_us),
        TOURNAMENT(tournament),
        TRAINING(training),
        OTHER(other)
    }

    companion object {
        const val championship = "CHAMPIONSHIP"
        const val friendly = "FRIENDLY"
        const val between_us = "BETWEEN_US"
        const val tournament = "TOURNAMENT"
        const val training = "TRAINING"
        const val other = "OTHER"
    }
}




