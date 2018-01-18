package com.zcorp.opensportmanagement.model

import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
@Table(name = "event")
open class Event(
        val type: EventType,
        @ManyToOne val opponent: Opponent,
        val dateTime: ZonedDateTime,
        @ManyToOne val stadium: Stadium,
        @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Event(type=$type, opponent=$opponent, dateTime=$dateTime, stadium=$stadium, id=$id)"
    }

    fun toDto(): EventDto = EventDto(
            id = this.id,
            type = this.type,
            opponent = this.opponent,
            date = this.dateTime.toLocalDateTime())
}

class EventDto(val id: Int, val type: EventType, val opponent: Opponent, val date: LocalDateTime)

const val championship = "CHAMPIONSHIP"
const val friendly = "FRIENDLY"
const val between_us = "BETWEEN_US"
const val tournament = "TOURNAMENT"
const val training = "TRAINING"

enum class EventType(val type: String) {
    CHAMPIONSHIP(championship),
    FRIENDLY(friendly),
    BETWEEN_US(between_us),
    TOURNAMENT(tournament),
    TRAINING(training);
}