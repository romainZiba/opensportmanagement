package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.ResultDto
import com.zcorp.opensportmanagement.repository.MatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class MatchService @Autowired constructor(
    private val matchRepository: MatchRepository
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
}