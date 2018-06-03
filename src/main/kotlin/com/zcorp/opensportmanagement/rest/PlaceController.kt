package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/places")
open class PlaceController @Autowired constructor(
    private val placeService: PlaceService,
    private val accessController: AccessController
) {

    @GetMapping("/{placeId}")
    open fun getPlace(
        @PathVariable("placeId") placeId: Int,
        authentication: Authentication
    ): ResponseEntity<PlaceDto> {
        val stadiumDto = placeService.getPlace(placeId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, stadiumDto.teamId!!)) {
            return ResponseEntity.ok(stadiumDto)
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{placeId}")
    open fun deletePlace(
        @PathVariable("placeId") placeId: Int,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val placeDto = placeService.getPlace(placeId)
        if (accessController.isTeamAdmin(authentication, placeDto.teamId!!)) {
            try {
                placeService.delete(placeId)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().body("Place $placeId is used")
            }
            return ResponseEntity.ok().build()
        }
        throw UserForbiddenException()
    }
}
