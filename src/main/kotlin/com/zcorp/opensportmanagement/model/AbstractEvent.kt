package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
abstract class AbstractEvent {

    @GeneratedValue
    @Id
    var id: Int = -1

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
    val team: Team

    @ManyToMany
    private val presentPlayers: MutableSet<TeamMember>
    @ManyToMany
    private val absentPlayers: MutableSet<TeamMember>

    private constructor(name: String, description: String, team: Team) {
        this.name = name
        this.description = description
        this.team = team
        this.presentPlayers = mutableSetOf()
        this.absentPlayers = mutableSetOf()
    }

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, stadium: Stadium,
                team: Team) : this(name, description, team) {
        this.recurrent = false
        this.fromDateTime = fromDateTime
        this.toDateTime = toDateTime
        this.stadium = stadium
    }

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, place: String,
                team: Team) : this(name, description, team) {
        this.fromDateTime = fromDateTime
        this.toDateTime = toDateTime
        this.place = place
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                team: Team) : this(name, description, team) {
        this.reccurenceDays = reccurenceDays
        this.recurrenceFromDate = recurrenceFromDate
        this.recurrenceToDate = recurrenceToDate
        this.recurrenceFromTime = recurrenceFromTime
        this.recurrenceToTime = recurrenceToTime
        this.stadium = stadium
    }

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                team: Team) : this(name, description, team) {
        this.reccurenceDays = reccurenceDays
        this.recurrenceFromDate = recurrenceFromDate
        this.recurrenceToDate = recurrenceToDate
        this.recurrenceFromTime = recurrenceFromTime
        this.recurrenceToTime = recurrenceToTime
        this.place = place
    }

    fun getPresentPlayers(): Set<TeamMember> {
        return presentPlayers
    }

    fun getAbsentPlayers(): Set<TeamMember> {
        return absentPlayers
    }

    abstract fun toDto(): EventDto

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

    fun parcipate(player: TeamMember, present: Boolean): AbstractEvent {
        if (present) {
            if (presentPlayers.size < Match.MAX_PLAYERS) {
                presentPlayers.add(player)
                absentPlayers.remove(player)
            }
        } else {
            presentPlayers.remove(player)
            absentPlayers.add(player)
        }
        return this
    }

    enum class EventType(val type: String) {
        MATCH(match),
        TRAINING(training),
        OTHER(other)
    }

    companion object {
        const val match = "MATCH"
        const val training = "TRAINING"
        const val other = "OTHER"
    }
}




