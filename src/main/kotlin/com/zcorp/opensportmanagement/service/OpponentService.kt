package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class OpponentService @Autowired constructor(private val opponentRepository: OpponentRepository) {

    @Transactional
    open fun getOpponent(opponentId: Int): Opponent? {
        return opponentRepository.getOne(opponentId)
    }

    @Transactional
    open fun deleteOpponent(opponentId: Int) {
        opponentRepository.deleteById(opponentId)
    }
}