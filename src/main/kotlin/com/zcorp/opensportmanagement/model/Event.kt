package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(AbstractEvent.other)
class Event : AbstractEvent {
    constructor(name: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                stadium: Stadium, team: Team) :
            super(name, fromDateTime, toDateTime, stadium, team)

    constructor(name: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                place: String, team: Team) :
            super(name, fromDateTime, toDateTime, place, team)

    override fun toDto(): EventDto {
        return EventDto(this.id, this.name, this.fromDateTime, this.toDateTime,
                this.place,
                this.stadium?.id,
                this.getPresentMembers().map { it.toDto() }.toList(),
                this.getAbsentMembers().map { it.toDto() }.toList())
    }
}