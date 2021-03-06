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
        this.team = builder.team
        this.fromDateTime = builder.fromDateTime
        this.toDateTime = builder.toDateTime
        this.place = builder.place
        this.maxMembers = builder.maxMembers
        this.opponent = builder.opponent
        this.championship = builder.championship
        this.type = builder.type
        this.isTeamLocal = builder.isTeamLocal
    }

    override fun toDto(): EventDto {
        val eventDto = EventDto(
                _id = this.id,
                name = this.name,
                fromDateTime = this.fromDateTime,
                toDateTime = this.toDateTime,
                placeId = this.place.id!!,
                placeName = this.place.name,
                presentMembers = this.membersResponse
                        .filter { it.status == MemberResponse.Status.PRESENT }
                        .map { it.teamMember.toDto() }.toList(),
                absentMembers = this.membersResponse
                        .filter { it.status == MemberResponse.Status.ABSENT }
                        .map { it.teamMember.toDto() }.toList(),
                waitingMembers = this.membersResponse
                        .filter { it.status == MemberResponse.Status.WAITING }
                        .map { it.teamMember.toDto() }.toList(),
                teamId = team.id!!,
                cancelled = this.cancelled,
                openForRegistration = this.openForRegistration)
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
        lateinit var team: Team
        lateinit var fromDateTime: LocalDateTime
        var toDateTime: LocalDateTime? = null
        lateinit var place: Place
        var maxMembers: Int = MAX_MEMBERS
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

        fun fromDateTime(fromDateTime: LocalDateTime): Builder {
            this.fromDateTime = fromDateTime
            return this
        }

        fun toDateTime(toDateTime: LocalDateTime?): Builder {
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