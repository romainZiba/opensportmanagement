package com.zcorp.opensportmanagement.model

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@IdClass(EventMemberId::class)
@Table(name = "member_response", uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("event_id", "member_id")))])
data class MemberResponse(
    @Id @ManyToOne @JoinColumn(name = "event_id") val event: AbstractEvent,
    @Id @ManyToOne @JoinColumn(name = "member_id") val teamMember: TeamMember,
    @Enumerated(EnumType.STRING) @Column(name = "status") val status: Status,
    @Column(name = "response_date") val subscriptionDate: LocalDateTime = LocalDateTime.now()
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (other !is MemberResponse) return false
        return (teamMember == other.teamMember && event == other.event)
    }

    override fun hashCode(): Int {
        var result = event.hashCode()
        result = 31 * result + teamMember.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + subscriptionDate.hashCode()
        return result
    }

    enum class Status {
        PRESENT,
        ABSENT,
        WAITING
    }
}

data class EventMemberId(val event: Int = 0, val teamMember: Int = 0) : Serializable
