package com.zcorp.opensportmanagement.model

import java.time.ZonedDateTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue(value = championship)
class ChampionshipEvent : Event {
    constructor(opponent: Opponent,
                dateTime: ZonedDateTime,
                stadium: Stadium) : super(EventType.CHAMPIONSHIP, opponent, dateTime, stadium)
}