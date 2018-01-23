package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "match")
class Match : Event {

    @ManyToOne val opponent: Opponent
    @ManyToOne @JsonBackReference val championship: Championship
    @ManyToMany val presentPlayers: MutableSet<ApplicationUser>
    @ManyToMany val notPresentPlayers: MutableSet<ApplicationUser>


    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, stadium: Stadium,
                opponent: Opponent, team: Team, championship: Championship) :
            super(name, description, fromDateTime, toDateTime, stadium, team) {
        this.opponent = opponent
        this.championship = championship
        this.presentPlayers = mutableSetOf()
        this.notPresentPlayers = mutableSetOf()
    }

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, place: String,
                opponent: Opponent, team: Team, championship: Championship) :
            super(name, description, fromDateTime, toDateTime, place, team) {
        this.opponent = opponent
        this.championship = championship
        this.presentPlayers = mutableSetOf()
        this.notPresentPlayers = mutableSetOf()
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                opponent: Opponent, team: Team, championship: Championship) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, stadium, team) {
        this.opponent = opponent
        this.championship = championship
        this.presentPlayers = mutableSetOf()
        this.notPresentPlayers = mutableSetOf()
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                opponent: Opponent, team: Team, championship: Championship) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, place, team) {
        this.opponent = opponent
        this.championship = championship
        this.presentPlayers = mutableSetOf()
        this.notPresentPlayers = mutableSetOf()
    }

    fun parcipate(player: ApplicationUser, present: Boolean) {
        if (present) {
            if (presentPlayers.size < MAX_PLAYERS) {
                presentPlayers.add(player)
                notPresentPlayers.remove(player)
            }
        } else {
            presentPlayers.remove(player)
            notPresentPlayers.add(player)
        }
    }

    
}


const val MAX_PLAYERS: Int = 10

class MatchDto(val name: String, val description: String, val fromDateTime: LocalDateTime, val toDateTime: LocalDateTime,
               val stadiumName: String, val opponentName: String)