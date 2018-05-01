package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
@DiscriminatorValue(AbstractEvent.match)
class Match : AbstractEvent {

    @ManyToOne
    val opponent: Opponent
    @ManyToOne
    @JsonBackReference
    val championship: Championship

    val isTeamLocal: Boolean
    var isDone: Boolean = false
    var teamScore: Int = 0
    var opponentScore: Int = 0

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, stadium: Stadium,
                opponent: Opponent, team: Team, championship: Championship, isLocal: Boolean = true) :
            super(name, description, fromDateTime, toDateTime, stadium, team) {
        this.opponent = opponent
        this.championship = championship
        this.isTeamLocal = isLocal
    }

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, place: String,
                opponent: Opponent, team: Team, championship: Championship, isLocal: Boolean = true) :
            super(name, description, fromDateTime, toDateTime, place, team) {
        this.opponent = opponent
        this.championship = championship
        this.isTeamLocal = isLocal
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                opponent: Opponent, team: Team, championship: Championship, isLocal: Boolean = true) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, stadium, team) {
        this.opponent = opponent
        this.championship = championship
        this.isTeamLocal = isLocal
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                opponent: Opponent, team: Team, championship: Championship, isLocal: Boolean = true) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, place, team) {
        this.opponent = opponent
        this.championship = championship
        this.isTeamLocal = isLocal
    }

    companion object {
        //TODO: handle configuration of this parameter
        const val MAX_PLAYERS: Int = 10
    }
}



