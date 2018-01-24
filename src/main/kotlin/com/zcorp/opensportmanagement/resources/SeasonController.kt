package com.zcorp.opensportmanagement.resources

import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.ChampionshipDto
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
class SeasonController(private val seasonRepository: SeasonRepository,
                       private val championshipRepository: ChampionshipRepository) {

    @GetMapping("/seasons")
    fun findAll() = seasonRepository.findAll()

    @PostMapping("/seasons/{seasonId}/championships")
    fun createChampionship(@NotNull @PathVariable("seasonId") seasonId: Int,
                           @RequestBody championshipDto: ChampionshipDto): ResponseEntity<Championship> {
        val season = seasonRepository.findOne(seasonId)
        if (season != null) {
            if (season.championships.map { it.name }.contains(championshipDto.name)) {
                throw EntityAlreadyExistsException("Championship " + championshipDto.name + " already exists")
            } else {
                val championship = Championship(championshipDto.name, season, mutableSetOf())
                val championshipSaved = championshipRepository.save(championship)
                return ResponseEntity(championshipSaved, HttpStatus.CREATED)
            }
        } else {
            throw EntityNotFoundException("Season $seasonId does not exist")
        }
    }
}