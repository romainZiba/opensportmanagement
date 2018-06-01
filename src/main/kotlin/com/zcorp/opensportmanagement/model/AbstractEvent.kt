package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.DiscriminatorColumn
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.JoinColumn
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type")
abstract class AbstractEvent protected constructor() {

    lateinit var name: String
    @ManyToOne @JoinColumn(name = "team_id") lateinit var team: Team
    lateinit var fromDateTime: LocalDateTime
    var toDateTime: LocalDateTime? = null
    @ManyToMany val presentMembers: MutableSet<TeamMember> = mutableSetOf()
    @ManyToMany val absentMembers: MutableSet<TeamMember> = mutableSetOf()
    @ManyToMany val waitingMembers: MutableSet<TeamMember> = mutableSetOf()
    @GeneratedValue @Id var id: Int = -1

    @ManyToOne
    lateinit var place: Place
    var maxMembers = MAX_MEMBERS

    @Column(name = "eventtype")
    var eventType: EventType ? = null

    @Column(name = "notified")
    var notified: Boolean = false

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
        const val MAX_MEMBERS: Int = 100
    }
}