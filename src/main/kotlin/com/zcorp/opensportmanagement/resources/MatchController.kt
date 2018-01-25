package com.zcorp.opensportmanagement.resources

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.security.IAuthenticationFacade
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull

@RestController
class MatchController(private val authenticationFacade: IAuthenticationFacade,
                      private val userRepository: UserRepository,
                      private val matchRepository: MatchRepository,
                      private val accessController: AccessController) {

    @GetMapping("/teams/{teamId}/matches")
    fun findAll(@PathVariable teamId: Int, authentication: Authentication) {
        if (accessController.isUserAllowedToAccessTeam(authentication, teamId)) matchRepository.findAll()
    }

    @PutMapping("/matches/{matchId}/{present}")
    fun participate(@NotNull @PathVariable("matchId") matchId: Int,
                    @NotNull @PathVariable("present") present: Boolean): Match {

        //TODO: handle access rights
        var match = matchRepository.findOne(matchId)
        if (match != null) {
            val authentication = authenticationFacade.authentication
            val user = userRepository.findByUsername(authentication.name)
            match = match.parcipate(user!!, present)
            matchRepository.save(match)
            return match
        }
        throw EntityNotFoundException("Match not found")
    }
}