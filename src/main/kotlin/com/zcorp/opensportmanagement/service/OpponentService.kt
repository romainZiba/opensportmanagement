package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class OpponentService @Autowired constructor(private val opponentRepository: OpponentRepository) {

    @Transactional
    open fun getOpponent(opponentId: Int): OpponentDto {
        return opponentRepository.findById(opponentId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Opponent $opponentId does not exist") }
    }

    @Transactional
    open fun deleteOpponent(opponentId: Int) {
        opponentRepository.deleteById(opponentId)
    }
}