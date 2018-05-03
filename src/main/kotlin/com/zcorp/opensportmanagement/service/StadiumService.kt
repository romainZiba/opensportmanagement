package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.StadiumDto
import com.zcorp.opensportmanagement.model.Stadium
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class StadiumService @Autowired constructor(private val stadiumRepository: StadiumRepository) {

    @Transactional
    open fun getStadium(stadiumId: Int): Stadium? {
        return stadiumRepository.getOne(stadiumId)
    }
}