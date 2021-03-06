package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.ChampionshipCreationDto
import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.ChampionshipService
import com.zcorp.opensportmanagement.service.SeasonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/seasons")
open class SeasonController @Autowired constructor(
    private val seasonService: SeasonService,
    private val championshipService: ChampionshipService,
    private val accessController: AccessController
) {

    @GetMapping("/{seasonId}")
    fun getSeason(
        @PathVariable("seasonId") seasonId: Int,
        authentication: Authentication
    ): SeasonDto {
        val seasonDto = seasonService.getSeason(seasonId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, seasonDto.teamId!!)) {
            return seasonDto
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{seasonId}")
    fun deleteSeason(
        @PathVariable("seasonId") seasonId: Int,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val seasonDto = seasonService.getSeason(seasonId)
        if (accessController.isTeamAdmin(authentication, seasonDto.teamId!!)) {
            seasonService.deleteSeason(seasonId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    @GetMapping("/{seasonId}/championships")
    open fun getChampionships(
        @PathVariable("seasonId") seasonId: Int,
        authentication: Authentication
    ): ResponseEntity<List<ChampionshipDto>> {
        val seasonDto = seasonService.getSeason(seasonId)
        if (accessController.isAccountAllowedToAccessTeam(authentication, seasonDto.teamId!!)) {
            val championships = championshipService.getChampionships(seasonId)
            return ResponseEntity.ok(championships)
        }
        throw UserForbiddenException()
    }

    @PostMapping("/{seasonId}/championships")
    open fun createChampionship(
        @PathVariable("seasonId") seasonId: Int,
        @RequestBody dto: ChampionshipCreationDto,
        authentication: Authentication
    ): ResponseEntity<ChampionshipDto> {
        val seasonDto = seasonService.getSeason(seasonId)
        if (accessController.isTeamAdmin(authentication, seasonDto.teamId!!)) {
            val savedChampionship = seasonService.createChampionship(dto, seasonId)
            return ResponseEntity(savedChampionship, HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }
}