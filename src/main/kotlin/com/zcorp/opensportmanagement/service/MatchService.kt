package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.dto.ResultDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.*
import javassist.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
open class MatchService @Autowired constructor(private val matchRepository: MatchRepository,
                                               private val teamRepository: TeamRepository,
                                               private val stadiumRepository: StadiumRepository,
                                               private val championshipRepository: ChampionshipRepository,
                                               private val opponentRepository: OpponentRepository) {

    @Transactional
    open fun getMatch(matchId: Int): EventDto {
        try {
            return matchRepository.getOne(matchId).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Match $matchId does not exist")
        }
    }

    @Transactional
    open fun changeScore(matchId: Int, resultDto: ResultDto): EventDto {
        val match = matchRepository.getOne(matchId)
        match.isDone = true
        match.teamScore = resultDto.teamScore
        match.opponentScore = resultDto.opponentScore
        return matchRepository.save(match).toDto()
    }

    @Transactional
    open fun createMatch(teamId: Int, dto: MatchCreationDto) {
        try {
            val team = teamRepository.getOne(teamId)
            var matchBuilder = Match.Builder()

            matchBuilder = matchBuilder.name(dto.name)
                    .fromDate(dto.fromDate)
                    .toDate(dto.toDate)
                    .team(team)
                    .type(dto.matchType)

            matchBuilder = if (dto.stadiumId != null) {
                val stadium = stadiumRepository.getOne(dto.stadiumId) ?: throw EntityNotFoundException()
                matchBuilder.stadium(stadium)
            } else {
                val p = dto.place ?: throw MissingParameterException("place")
                matchBuilder.place(p)
            }

            if (dto.matchType != Match.MatchType.BETWEEN_US) {
                val championshipId = dto.championshipId ?: throw MissingParameterException("championshipId")
                val opponentId = dto.opponentId ?: throw MissingParameterException("opponentId")

                val championship = championshipRepository.getOne(championshipId)
                val opponent = opponentRepository.getOne(opponentId)
                val isTeamLocal = dto.isTeamLocal ?: throw MissingParameterException("isTeamLocal")

                matchBuilder = matchBuilder.championship(championship)
                        .opponent(opponent)
                        .isTeamLocal(isTeamLocal)
            }
            matchRepository.save(matchBuilder.build())
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Team $teamId does not exist")
        }

    }
}