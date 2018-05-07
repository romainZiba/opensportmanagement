package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.OpponentDto
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import javassist.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
open class OpponentService @Autowired constructor(private val opponentRepository: OpponentRepository) {

    @Transactional
    open fun getOpponent(opponentId: Int): OpponentDto {
        try {
            return opponentRepository.getOne(opponentId).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Opponent $opponentId does not exist")
        }
    }

    @Transactional
    open fun deleteOpponent(opponentId: Int) {
        opponentRepository.deleteById(opponentId)
    }
}