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
        this.eventType = builder.eventType
        this.team = builder.team
        this.fromDateTime = builder.fromDateTime
        this.toDateTime = builder.toDateTime
        this.place = builder.place
        this.maxMembers = builder.maxMembers
    }

    override fun toDto(): EventDto {
        return EventDto(this.id, this.name, this.fromDateTime, this.toDateTime,
                this.place.id,
                this.getPresentMembers().map { it.toDto() }.toList(),
                this.getAbsentMembers().map { it.toDto() }.toList(),
                this.getWaitingMembers().map { it.toDto() }.toList(),
                team.id)
    }

    class Builder {
        lateinit var name: String
        var eventType: EventType ? = null
        lateinit var team: Team
        lateinit var fromDateTime: LocalDateTime
        var toDateTime: LocalDateTime? = null
        lateinit var place: Place
        var maxMembers: Int = MAX_PLAYERS

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

        fun type(t: EventType): Builder {
            this.eventType = t
            return this
        }

        fun build(): Event {
            return Event(this)
        }
    }
}