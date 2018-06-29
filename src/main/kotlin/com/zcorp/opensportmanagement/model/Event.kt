package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(AbstractEvent.other)
class Event private constructor(builder: Builder) : AbstractEvent() {

    init {
        this.name = builder.name
        this.team = builder.team
        this.fromDateTime = builder.fromDateTime
        this.toDateTime = builder.toDateTime
        this.place = builder.place
        this.maxMembers = builder.maxMembers
    }

    override fun toDto(): EventDto {
        return EventDto(
                _id = this.id,
                name = this.name,
                fromDateTime = this.fromDateTime,
                toDateTime = this.toDateTime,
                placeId = this.place.id!!,
                presentMembers = this.membersResponse
                        .filter { it.status == MemberResponse.Status.PRESENT }
                        .map { it.teamMember.toDto() }.toList(),
                absentMembers = this.membersResponse
                        .filter { it.status == MemberResponse.Status.ABSENT }
                        .map { it.teamMember.toDto() }.toList(),
                waitingMembers = this.membersResponse
                        .filter { it.status == MemberResponse.Status.WAITING }
                        .map { it.teamMember.toDto() }.toList(),
                teamId = team.id,
                cancelled = this.cancelled)
    }

    class Builder {
        lateinit var name: String
        lateinit var team: Team
        lateinit var fromDateTime: LocalDateTime
        var toDateTime: LocalDateTime? = null
        lateinit var place: Place
        var maxMembers: Int = MAX_MEMBERS

        fun name(n: String): Builder {
            this.name = n
            return this
        }

        fun team(t: Team): Builder {
            this.team = t
            return this
        }

        fun fromDate(fromDateTime: LocalDateTime): Builder {
            this.fromDateTime = fromDateTime
            return this
        }

        fun toDate(toDateTime: LocalDateTime?): Builder {
            this.toDateTime = toDateTime
            return this
        }

        fun place(p: Place): Builder {
            this.place = p
            return this
        }

        fun maxMembers(max: Int): Builder {
            this.maxMembers = max
            return this
        }

        fun build(): Event {
            return Event(this)
        }
    }
}