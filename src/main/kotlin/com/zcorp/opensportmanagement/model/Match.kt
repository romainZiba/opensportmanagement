package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
@DiscriminatorValue(AbstractEvent.match)
class Match : AbstractEvent {

    @ManyToOne
    val opponent: Opponent
    @ManyToOne
    @JsonBackReference
    val championship: Championship

    val isTeamLocal: Boolean
    var isDone: Boolean = false
    var teamScore: Int = 0
    var opponentScore: Int = 0

    constructor(name: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, stadium: Stadium,
                opponent: Opponent, team: Team, championship: Championship, isLocal: Boolean = true) :
            super(name, fromDateTime, toDateTime, stadium, team) {
        this.opponent = opponent
        this.championship = championship
        this.isTeamLocal = isLocal
    }

    constructor(name: String, fromDateTime: LocalDateTime, toDateTime: LocalDateTime, place: String,
                opponent: Opponent, team: Team, championship: Championship, isLocal: Boolean = true) :
            super(name, fromDateTime, toDateTime, place, team) {
        this.opponent = opponent
        this.championship = championship
        this.isTeamLocal = isLocal
    }

    override fun toDto(): EventDto {

        val eventDto = EventDto(this.id, this.name, this.fromDateTime, this.toDateTime,
                this.place,
                this.stadium?.id,
                this.getPresentMembers().map { it.toDto() }.toList(),
                this.getAbsentMembers().map { it.toDto() }.toList(),
                this.getWaitingMembers().map { it.toDto() }.toList())
        eventDto.isDone = this.isDone
        if (this.isTeamLocal) {
            eventDto.localTeamName = this.team.name
            eventDto.localTeamImgUrl = this.team.imgUrl
            eventDto.localTeamScore = this.teamScore
            eventDto.visitorTeamName = this.opponent.name
            eventDto.visitorTeamImgUrl = this.opponent.imgUrl
            eventDto.visitorTeamScore = this.opponentScore
        } else {
            eventDto.localTeamName = this.opponent.name
            eventDto.localTeamImgUrl = this.opponent.imgUrl
            eventDto.localTeamScore = this.opponentScore
            eventDto.visitorTeamName = this.team.name
            eventDto.visitorTeamImgUrl = this.team.imgUrl
            eventDto.visitorTeamScore = this.teamScore
        }
        return eventDto
    }
}



