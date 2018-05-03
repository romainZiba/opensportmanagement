package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.MatchDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.rest.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class ChampionshipService @Autowired constructor(private val championshipRepository: ChampionshipRepository,
                                                      private val stadiumRepository: StadiumRepository,
                                                      private val opponentRepository: OpponentRepository,
                                                      private val matchRepository: MatchRepository) {

    @Transactional
    open fun getChampionship(championshipId: Int): Championship? {
        return championshipRepository.getOne(championshipId)
    }

    @Transactional
    open fun deleteChampionship(championshipId: Int) {
        // TODO: delete all matches
        championshipRepository.deleteById(championshipId)
    }

    @Transactional
    open fun createMatch(matchDto: MatchDto, championshipId: Int): Match {
        val championship = championshipRepository.getOne(championshipId)
        val opponentName = matchDto.opponentName
        val stadiumName = matchDto.stadiumName
        val stadium = stadiumRepository.findByName(stadiumName) ?: throw EntityNotFoundException("Stadium $stadiumName does not exist")
        val opponent = opponentRepository.findByName(opponentName) ?: throw EntityNotFoundException("OpponentDto $opponentName does not exist")
        val match = Match(matchDto.name, matchDto.description, matchDto.fromDateTime, matchDto.toDateTime,
                stadium, opponent, championship.season.team, championship)
        return matchRepository.save(match)
    }
}