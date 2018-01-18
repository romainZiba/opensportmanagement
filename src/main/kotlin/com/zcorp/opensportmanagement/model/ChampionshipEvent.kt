package com.zcorp.opensportmanagement.model

import java.time.ZonedDateTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
@DiscriminatorValue(value = championship)
class ChampionshipEvent : Event {
    @ManyToOne val championship: Championship

    constructor(championship: Championship,
                opponent: Opponent,
                dateTime: ZonedDateTime,
                stadium: Stadium) : super(EventType.CHAMPIONSHIP, opponent, dateTime, stadium) {
        this.championship = championship
    }

    override fun toString(): String {
        return "Event(type=$type, opponent=$opponent, dateTime=$dateTime, stadium=$stadium, championship=$championship, id=$id)"
    }


}