package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.services.TeamRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TeamController(private val teamRepository: TeamRepository) {

    @GetMapping("/teams")
    fun findAll() = teamRepository.findAll().map { it.toDto() }.toList()

    @GetMapping("/teams/{name}")
    fun findByLastName(@PathVariable name: String) = teamRepository.findByName(name).toDto()
}