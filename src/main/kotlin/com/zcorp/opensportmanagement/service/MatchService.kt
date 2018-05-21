package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.dto.ResultDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class MatchService @Autowired constructor(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
    private val placeRepository: PlaceRepository,
    private val championshipRepository: ChampionshipRepository,
    private val opponentRepository: OpponentRepository
) {

    @Transactional
    open fun getMatch(matchId: Int): EventDto {
        return matchRepository.findById(matchId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Match $matchId does not exist") }
    }

    @Transactional
    open fun changeScore(matchId: Int, resultDto: ResultDto): EventDto {
        val match = matchRepository.findById(matchId).orElseThrow { NotFoundException("Match $matchId does not exist") }
        match.isDone = true
        match.teamScore = resultDto.teamScore
        match.opponentScore = resultDto.opponentScore
        return matchRepository.save(match).toDto()
    }

    @Transactional
    open fun createMatch(teamId: Int, dto: MatchCreationDto): EventDto {
        val team = teamRepository.findById(teamId).orElseThrow { NotFoundException("Team $teamId does not exist") }
        var matchBuilder = Match.Builder()

        matchBuilder = matchBuilder.name(dto.name)
                .fromDate(dto.fromDate)
                .toDate(dto.toDate)
                .team(team)
                .type(dto.matchType)

        val stadium = placeRepository.findById(dto.placeId)
                .orElseThrow { NotFoundException("Place ${dto.placeId} does not exist") }
        matchBuilder.place(stadium)

        if (dto.matchType != Match.MatchType.BETWEEN_US) {
            val championshipId = dto.championshipId ?: throw MissingParameterException("championshipId")
            val opponentId = dto.opponentId ?: throw MissingParameterException("opponentId")

            val championship = championshipRepository.findById(championshipId)
                    .orElseThrow { NotFoundException("Championship $championshipId does not exist") }
            val opponent = opponentRepository.findById(opponentId)
                    .orElseThrow { NotFoundException("Opponent $opponentId does not exist") }
            val isTeamLocal = dto.isTeamLocal ?: throw MissingParameterException("isTeamLocal")

            matchBuilder = matchBuilder.championship(championship)
                    .opponent(opponent)
                    .isTeamLocal(isTeamLocal)
        }
        return matchRepository.save(matchBuilder.build()).toDto()
    }
}