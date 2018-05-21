package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class PlaceService @Autowired constructor(
    private val placeRepository: PlaceRepository,
    private val teamRepository: TeamRepository
) {

    @Transactional
    open fun getPlace(placeId: Int): PlaceDto {
        return placeRepository.findById(placeId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Place $placeId does not exist") }
    }

    @Transactional
    open fun createPlace(placeDto: PlaceDto, teamId: Int): PlaceDto {
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        val stadium = Place(placeDto.name, placeDto.address, placeDto.city, team)
        return placeRepository.save(stadium).toDto()
    }
}