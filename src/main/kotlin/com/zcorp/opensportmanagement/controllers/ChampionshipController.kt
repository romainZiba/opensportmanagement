package com.zcorp.opensportmanagement.controllers

import com.zcorp.opensportmanagement.UserForbiddenException
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.*
import com.zcorp.opensportmanagement.security.AccessController
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChampionshipController(private val championshipRepository: ChampionshipRepository,
                             private val opponentRepository: OpponentRepository,
                             private val eventRepository: EventRepository,
                             private val stadiumRepository: StadiumRepository,
                             private val seasonRepository: SeasonRepository,
                             private val accessController: AccessController) {

    @PostMapping("/seasons/{seasonId}/championships")
    fun createChampionship(@PathVariable("seasonId") seasonId: Int,
                           @RequestBody championshipDto: ChampionshipDto,
                           authentication: Authentication) {
        val season = seasonRepository.findOne(seasonId)
        if (season != null) {
            val teamId = season.team.id
            if (accessController.isTeamAdmin(authentication, teamId)) {
                val championship = Championship(championshipDto.name, season)
                championshipRepository.save(championship)
            }
        }
        throw UserForbiddenException()
    }
}