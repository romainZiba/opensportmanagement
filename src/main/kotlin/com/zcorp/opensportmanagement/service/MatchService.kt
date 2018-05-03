package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.ResultDto
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.MatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class MatchService @Autowired constructor(private val matchRepository: MatchRepository) {

    @Transactional
    open fun getMatch(matchId: Int): Match? {
        return matchRepository.getOne(matchId)
    }

    @Transactional
    open fun changeScore(match: Match, resultDto: ResultDto): Match {
        match.isDone = true
        match.teamScore = resultDto.teamScore
        match.opponentScore = resultDto.opponentScore
        return matchRepository.save(match)
    }
}