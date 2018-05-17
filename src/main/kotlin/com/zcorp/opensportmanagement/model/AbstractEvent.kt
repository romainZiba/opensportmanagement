package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
abstract class AbstractEvent protected constructor() {

    lateinit var name: String
    @ManyToOne @JoinColumn(name = "team_id") lateinit var team: Team
    lateinit var fromDateTime: LocalDateTime
    var toDateTime: LocalDateTime? = null
    @ManyToMany private val presentMembers: MutableSet<TeamMember> = mutableSetOf()
    @ManyToMany private val absentMembers: MutableSet<TeamMember> = mutableSetOf()
    @ManyToMany private val waitingMembers: MutableSet<TeamMember> = mutableSetOf()
    @GeneratedValue @Id var id: Int = -1

    @ManyToOne
    lateinit var place: Place
    var maxMembers = MAX_PLAYERS

    @Column(name = "eventtype")
    var eventType: EventType ? = null

    fun getAbsentMembers(): Set<TeamMember> {
        return absentMembers
    }

    fun getPresentMembers(): Set<TeamMember> {
        return presentMembers
    }

    fun getWaitingMembers(): Set<TeamMember> {
        return waitingMembers
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

    fun participate(player: TeamMember, present: Boolean): AbstractEvent {
        if (present) {
            if (presentMembers.size < maxMembers) {
                presentMembers.add(player)
                absentMembers.remove(player)
                waitingMembers.remove(player)
            } else {
                waitingMembers.add(player)
            }
        } else {
            presentMembers.remove(player)
            waitingMembers.remove(player)
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
        //TODO: handle configuration of this parameter
        const val MAX_PLAYERS: Int = 10

    }
}




