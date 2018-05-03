package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(AbstractEvent.other)
class Event : AbstractEvent {
    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                stadium: Stadium, team: Team) :
            super(name, description, fromDateTime, toDateTime, stadium, team)

    constructor(name: String, description: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime,
                place: String, team: Team) :
            super(name, description, fromDateTime, toDateTime, place, team)

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, stadium: Stadium,
                team: Team) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, stadium, team)

    constructor(name: String, description: String, reccurenceDays: MutableSet<DayOfWeek>, recurrenceFromDate: LocalDate,
                recurrenceToDate: LocalDate, recurrenceFromTime: LocalTime, recurrenceToTime: LocalTime, place: String,
                team: Team) :
            super(name, description, reccurenceDays, recurrenceFromDate, recurrenceToDate, recurrenceFromTime,
                    recurrenceToTime, place, team)

    override fun toDto(): EventDto {
        return EventDto(this.id, this.name, this.description, this.fromDateTime, this.toDateTime,
                this.place,
                this.stadium?.id,
                this.getPresentPlayers().map { it.user.username }.toList(),
                this.getAbsentPlayers().map { it.user.username}.toList())
    }
}