package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.SeasonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RepositoryRestController
@RequestMapping("/seasons")
open class SeasonController @Autowired constructor(private val seasonService: SeasonService,
                                                   private val accessController: AccessController) {

    @GetMapping("/{seasonId}")
    fun getSeason(@PathVariable("seasonId") seasonId: Int,
                  authentication: Authentication): SeasonDto {
        val season = seasonService.getSeason(seasonId) ?: throw UserForbiddenException()
        if (accessController.isUserAllowedToAccessTeam(authentication, season.team.id)) {
            return season.toDto()
        }
        throw UserForbiddenException()
    }

    @DeleteMapping("/{seasonId}")
    fun deleteSeason(@PathVariable("seasonId") seasonId: Int,
                     authentication: Authentication): ResponseEntity<Any> {
        val season = seasonService.getSeason(seasonId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, season.team.id)) {
            seasonService.deleteSeason(seasonId)
            return ResponseEntity.noContent().build()
        }
        throw UserForbiddenException()
    }

    //TODO
//    @GetMapping("/{seasonId}/championships")
//    open fun getChampionships(@PathVariable("seasonId") seasonId: Int, authentication: Authentication): ResponseEntity<List<ChampionshipResource>> {
//        val season = seasonRepository.getOne(seasonId) ?: throw UserForbiddenException()
//        if (accessController.isUserAllowedToAccessTeam(authentication, season.team.id)) {
//            return ResponseEntity.ok(season.championships.map { championship -> ChampionshipResource(championship) })
//        }
//        throw UserForbiddenException()
//    }

    @PostMapping("/{seasonId}/championships")
    open fun createChampionship(@PathVariable("seasonId") seasonId: Int,
                                @RequestBody championshipDto: ChampionshipDto,
                                authentication: Authentication): ResponseEntity<ChampionshipDto> {
        val season = seasonService.getSeason(seasonId) ?: throw UserForbiddenException()
        if (accessController.isTeamAdmin(authentication, season.team.id)) {
            val championshipDto = seasonService.createChampionship(championshipDto, season)
            return ResponseEntity(championshipDto, HttpStatus.CREATED)
        }
        throw UserForbiddenException()
    }
}