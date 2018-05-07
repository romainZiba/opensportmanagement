package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.StadiumDto
import com.zcorp.opensportmanagement.model.Stadium
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import javassist.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
open class StadiumService @Autowired constructor(private val stadiumRepository: StadiumRepository,
                                                 private val teamRepository: TeamRepository) {

    @Transactional
    open fun getStadium(stadiumId: Int): StadiumDto {
        try {
            return stadiumRepository.getOne(stadiumId).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Stadium $stadiumId does not exist")
        }
    }

    @Transactional
    open fun createStadium(stadiumDto: StadiumDto, teamId: Int): StadiumDto {
        try {
            val team = teamRepository.getOne(teamId)
            val stadium = Stadium(stadiumDto.name, stadiumDto.address, stadiumDto.city, team)
            return stadiumRepository.save(stadium).toDto()
        } catch (e: EntityNotFoundException) {
            throw NotFoundException("Team $teamId does not exist")
        }
    }
}