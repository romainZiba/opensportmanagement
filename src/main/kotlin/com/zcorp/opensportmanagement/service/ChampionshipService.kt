package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
open class ChampionshipService @Autowired constructor(
    private val championshipRepository: ChampionshipRepository,
    private val placeRepository: PlaceRepository,
    private val opponentRepository: OpponentRepository,
    private val matchRepository: MatchRepository
) {

    @Transactional
    open fun getChampionship(championshipId: Int): ChampionshipDto {
        try {
            return championshipRepository.getOne(championshipId).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Championship $championshipId does not exist")
        }
    }

    @Transactional
    open fun deleteChampionship(championshipId: Int) {
        matchRepository.deleteAllMatches(championshipId)
        championshipRepository.deleteById(championshipId)
    }

    @Transactional
    open fun createMatch(dto: MatchCreationDto, championshipId: Int): Match {
        try {
            val championship = championshipRepository.getOne(championshipId)
            val opponentId = dto.opponentId ?: throw MissingParameterException("opponentId")
            val stadiumId = dto.placeId ?: throw MissingParameterException("placeId")
            val stadium = placeRepository.getOne(stadiumId)
            val opponent = opponentRepository.getOne(opponentId)
            if (dto.fromDate.isBefore(LocalDateTime.now())) {
                throw PastEventException()
            }
            val match = Match.Builder()
                    .name(dto.name)
                    .fromDate(dto.fromDate)
                    .toDate(dto.toDate)
                    .place(stadium)
                    .opponent(opponent)
                    .team(championship.season.team)
                    .championship(championship)
                    .type(dto.matchType)
                    .build()
            return matchRepository.save(match)
        } catch (e: EntityNotFoundException) {
            throw NotFoundException(e.message ?: "")
        }
    }
}