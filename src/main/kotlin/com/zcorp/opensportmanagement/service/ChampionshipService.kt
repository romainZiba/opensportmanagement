package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.MatchDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.rest.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
open class ChampionshipService @Autowired constructor(private val championshipRepository: ChampionshipRepository,
                                                      private val stadiumRepository: StadiumRepository,
                                                      private val opponentRepository: OpponentRepository,
                                                      private val matchRepository: MatchRepository) {

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
    open fun createMatch(matchDto: MatchDto, championshipId: Int): Match {
        try {
            val championship = championshipRepository.getOne(championshipId)
            val opponentName = matchDto.opponentName
            val stadiumName = matchDto.stadiumName
            val stadium = stadiumRepository.findByName(stadiumName) ?: throw NotFoundException("Stadium $stadiumName does not exist")
            val opponent = opponentRepository.findByName(opponentName) ?: throw NotFoundException("Opponent $opponentName does not exist")
            val match = Match.Builder()
                    .name(matchDto.name)
                    .fromDate( matchDto.fromDateTime)
                    .toDate(matchDto.toDateTime)
                    .stadium(stadium)
                    .opponent(opponent)
                    .team(championship.season.team)
                    .championship(championship)
                    .build()
            return matchRepository.save(match)
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Championship $championshipId does not exist")
        }

    }
}