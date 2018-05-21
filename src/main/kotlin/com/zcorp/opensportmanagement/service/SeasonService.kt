package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import com.zcorp.opensportmanagement.dto.SeasonDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class SeasonService @Autowired constructor(
    private val seasonRepository: SeasonRepository,
    private val championshipRepository: ChampionshipRepository
) {

    @Transactional
    open fun getSeason(seasonId: Int): SeasonDto {
        return seasonRepository.findById(seasonId)
                .map { it.toDto() }
                .orElseThrow { NotFoundException("Season $seasonId does not exist") }
    }

    @Transactional
    open fun deleteSeason(seasonId: Int) {
        // TODO: delete championships
        seasonRepository.deleteById(seasonId)
    }

    @Transactional
    open fun createChampionship(championshipDto: ChampionshipDto, seasonId: Int): ChampionshipDto {
        val season = seasonRepository.findById(seasonId)
                .orElseThrow { NotFoundException("Season $seasonId does not exist") }
        val championship = Championship(championshipDto.name, season)
        return championshipRepository.save(championship).toDto()
    }
}