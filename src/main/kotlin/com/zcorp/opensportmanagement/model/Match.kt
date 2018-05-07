package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.EventDto
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
@DiscriminatorValue(AbstractEvent.match)
class Match private constructor(builder: Builder) : AbstractEvent() {

    @ManyToOne
    var opponent: Opponent
    @ManyToOne
    var championship: Championship

    var isTeamLocal: Boolean = true
    var isDone: Boolean = false
    var teamScore: Int = 0
    var opponentScore: Int = 0

    @Column(name = "matchtype")
    var type: MatchType

    init {
        this.name = builder.name
        this.eventType = builder.eventType
        this.team = builder.team
        this.fromDateTime = builder.fromDateTime
        this.toDateTime = builder.toDateTime
        this.stadium = builder.stadium
        this.place = builder.place
        this.maxMembers = builder.maxMembers
        this.opponent = builder.opponent
        this.championship = builder.championship
        this.type = builder.type
        this.isTeamLocal = builder.isTeamLocal
    }

    override fun toDto(): EventDto {
        val eventDto = EventDto(this.id, this.name, this.fromDateTime, this.toDateTime,
                this.place,
                this.stadium?.id,
                this.getPresentMembers().map { it.toDto() }.toList(),
                this.getAbsentMembers().map { it.toDto() }.toList(),
                this.getWaitingMembers().map { it.toDto() }.toList(),
                team.id)
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

    enum class MatchType {
        CHAMPIONSHIP,
        BETWEEN_US
    }

    class Builder {
        lateinit var name: String
        val eventType = EventType.MATCH
        lateinit var team: Team
        lateinit var fromDateTime: LocalDateTime
        var toDateTime: LocalDateTime? = null
        var stadium: Stadium? = null
        var place: String? = null
        var maxMembers: Int = MAX_PLAYERS
        lateinit var opponent: Opponent
        lateinit var championship: Championship
        lateinit var type: MatchType
        var isTeamLocal: Boolean = true


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

        fun stadium(s: Stadium): Builder {
            this.stadium = s
            return this
        }

        fun place(p: String): Builder {
            this.place = p
            return this
        }

        fun maxMembers(max: Int): Builder {
            this.maxMembers = max
            return this
        }

        fun opponent(o: Opponent): Builder {
            this.opponent = o
            return this
        }

        fun championship(c: Championship): Builder {
            this.championship = c
            return this
        }

        fun type(t: MatchType): Builder {
            this.type = t
            return this
        }

        fun isTeamLocal(b: Boolean): Builder {
            this.isTeamLocal = b
            return this
        }

        fun build(): Match {
            return Match(this)
        }
    }
}





