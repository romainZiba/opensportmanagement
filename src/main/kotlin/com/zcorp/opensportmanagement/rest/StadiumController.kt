package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.rest.resources.StadiumResource
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/stadiums")
open class StadiumController @Autowired constructor(private val stadiumRepository: StadiumRepository,
                                                    private val accessController: AccessController) {

    @GetMapping("/{stadiumId}")
    open fun getStadium(@PathVariable("stadiumId") stadiumId: Int,
                        authentication: Authentication): ResponseEntity<StadiumResource> {
        val stadium = stadiumRepository.findOne(stadiumId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, stadium.team.id)) {
            return ResponseEntity.ok(StadiumResource(stadium))
        }
        throw UserForbiddenException()
    }
}
