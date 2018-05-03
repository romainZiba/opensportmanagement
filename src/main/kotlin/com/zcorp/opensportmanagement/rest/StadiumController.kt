package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.StadiumDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.StadiumService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/stadiums")
open class StadiumController @Autowired constructor(private val stadiumService: StadiumService,
                                                    private val accessController: AccessController) {

    @GetMapping("/{stadiumId}")
    open fun getStadium(@PathVariable("stadiumId") stadiumId: Int,
                        authentication: Authentication): ResponseEntity<StadiumDto> {
        val stadium = stadiumService.getStadium(stadiumId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, stadium.team.id)) {
            return ResponseEntity.ok(stadium.toDto())
        }
        throw UserForbiddenException()
    }
}
