package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
abstract class AbstractEvent private constructor(val name: String,
                                                 @ManyToOne @JoinColumn(name = "team_id") val team: Team,
                                                 var fromDateTime: LocalDateTime,
                                                 var toDateTime: LocalDateTime,
                                                 @ManyToMany private val presentMembers: MutableSet<TeamMember> = mutableSetOf(),
                                                 @ManyToMany private val absentMembers: MutableSet<TeamMember> = mutableSetOf(),
                                                 @GeneratedValue @Id var id: Int = -1) {
    @ManyToOne
    var stadium: Stadium? = null
    var place: String? = null

    constructor(name: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, stadium: Stadium,
                team: Team) : this(name, team, fromDateTime, toDateTime) {
        this.stadium = stadium
    }

    constructor(name: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, place: String,
                team: Team) : this(name, team, fromDateTime, toDateTime) {
        this.place = place
    }

    fun getAbsentMembers(): Set<TeamMember> {
        return absentMembers
    }

    fun getPresentMembers(): Set<TeamMember> {
        return presentMembers
    }

    abstract fun toDto(): EventDto

    override fun toString(): String {
        return "AbstractEvent(name='$name', id=$id)"
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
            if (presentMembers.size < Match.MAX_PLAYERS) {
                presentMembers.add(player)
                absentMembers.remove(player)
            }
        } else {
            presentMembers.remove(player)
            absentMembers.add(player)
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




