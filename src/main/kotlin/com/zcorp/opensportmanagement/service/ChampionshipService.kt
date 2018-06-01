package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repository.ChampionshipRepository
import com.zcorp.opensportmanagement.repository.MatchRepository
import com.zcorp.opensportmanagement.repository.OpponentRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
open class ChampionshipService @Autowired constructor(
    private val championshipRepository: ChampionshipRepository,
    private val placeRepository: PlaceRepository,
    private val opponentRepository: OpponentRepository,
    private val matchRepository: MatchRepository
) {

    @Transactional
    @Throws(NotFoundException::class)
    open fun getChampionship(championshipId: Int): ChampionshipDto {
        return championshipRepository.findById(championshipId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Championship $championshipId does not exist") }
    }

    @Transactional
    open fun deleteChampionship(championshipId: Int) {
        matchRepository.deleteAllMatches(championshipId)
        championshipRepository.deleteById(championshipId)
    }

    @Transactional
    open fun createMatch(dto: MatchCreationDto, championshipId: Int, now: LocalDateTime): Match {
        val championship = championshipRepository.findById(championshipId)
                .orElseThrow { NotFoundException("Championship $championshipId does not exist") }
        val opponentId = dto.opponentId ?: throw MissingParameterException("opponentId")
        val stadiumId = dto.placeId
        val stadium = placeRepository.findById(stadiumId)
                .orElseThrow { NotFoundException("Stadium $stadiumId does not exist") }
        val opponent = opponentRepository.findById(opponentId)
                .orElseThrow { NotFoundException("Opponent $opponentId does not exist") }
        if (dto.fromDate.isBefore(now)) {
            throw NotPossibleException("From date can not be in the past")
        }
        val match = Match.Builder().name(dto.name).fromDate(dto.fromDate).toDate(dto.toDate).place(stadium)
                .opponent(opponent).team(championship.season.team).championship(championship).type(dto.matchType)
                .build()
        return matchRepository.save(match)
    }
}