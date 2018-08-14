package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Championship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import javax.transaction.Transactional

@Transactional(Transactional.TxType.MANDATORY)
@RepositoryRestResource(exported = false)
interface ChampionshipRepository : JpaRepository<Championship, Int> {
    fun findBySeasonId(seasonId: Int): List<Championship>
}