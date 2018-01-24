package com.zcorp.opensportmanagement.resources

import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.security.IAuthenticationFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotNull

@RestController
class MatchController(private val authenticationFacade: IAuthenticationFacade,
                      private val userRepository: UserRepository,
                      private val matchRepository: MatchRepository) {

    @GetMapping("/matches")
    fun findAll() = matchRepository.findAll()

    @PutMapping("/matches/{matchId}/{present}")
    fun participate(@NotNull @PathVariable("matchId") matchId: Int,
                    @NotNull @PathVariable("present") present: Boolean): Match {
        val match = matchRepository.findOne(matchId)
        if (match != null) {
            //TODO: handle session
            val authentication = authenticationFacade.authentication
            val user = userRepository.findByUsername(authentication.name)
            match.parcipate(user!!, present)
            return match
        }
        throw EntityNotFoundException("Match not found")
    }
}