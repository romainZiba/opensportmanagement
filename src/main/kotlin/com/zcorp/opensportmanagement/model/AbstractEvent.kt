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
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
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
    @OneToMany(mappedBy = "event") val membersResponse: MutableSet<MemberResponse> = mutableSetOf()
    @GeneratedValue @Id @Column(name = "event_id") var id: Int = -1

    @ManyToOne
    lateinit var place: Place
    var maxMembers = MAX_MEMBERS

    @Column(name = "registration_open")
    var openForRegistration: Boolean = false

    @Column(name = "cancelled")
    var cancelled: Boolean = false

    fun isFull(): Boolean {
        return membersResponse.filter { it.status == MemberResponse.Status.PRESENT }.size == maxMembers
    }

    fun isMemberPresent(username: String): Boolean {
        return membersResponse
                .filter { memberResponse -> memberResponse.status == MemberResponse.Status.PRESENT }
                .map { memberResponse -> memberResponse.teamMember.account.username }
                .any { name -> name == username }
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

    enum class EventType(val type: String) {
        MATCH(match),
        TRAINING(training),
        OTHER(other)
    }

    companion object {
        const val match = "MATCH"
        const val other = "OTHER"
        const val training = "TRAINING"
        const val MAX_MEMBERS: Int = 100
    }
}