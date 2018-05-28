package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class PlaceService @Autowired constructor(
    private val placeRepository: PlaceRepository
) {

    @Transactional
    open fun getPlace(placeId: Int): PlaceDto {
        return placeRepository.findById(placeId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Place $placeId does not exist") }
    }

    @Transactional
    open fun delete(placeId: Int) {
        placeRepository.deleteById(placeId)
    }
}