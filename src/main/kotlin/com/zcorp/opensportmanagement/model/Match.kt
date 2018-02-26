package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.zcorp.opensportmanagement.rest.MatchController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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

    @ManyToOne
    val opponent: Opponent
    @ManyToOne
    @JsonBackReference
    val championship: Championship
    @ManyToMany
    val presentPlayers: MutableSet<TeamMember>
    @ManyToMany
    val notPresentPlayers: MutableSet<TeamMember>

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

    fun parcipate(player: TeamMember, present: Boolean): Match {
        if (present) {
            if (presentPlayers.size < MAX_PLAYERS) {
                presentPlayers.add(player)
                notPresentPlayers.remove(player)
            }
        } else {
            presentPlayers.remove(player)
            notPresentPlayers.add(player)
        }
        return this
    }
}


// Resource with self links
class MatchResource(val id: Int, val opponent: Opponent, val presentPlayers: Set<TeamMember>, val notPresentPlayers: Set<TeamMember>, championshipId: Int) : ResourceSupport() {
    constructor(m: Match) : this(m.id, m.opponent, m.presentPlayers, m.notPresentPlayers, m.championship.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MatchController::class.java).getMatches(championshipId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}


const val MAX_PLAYERS: Int = 10

class MatchDto(val name: String, val description: String, val fromDateTime: LocalDateTime, val toDateTime: LocalDateTime,
               val stadiumName: String, val opponentName: String)