package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Championship
import org.springframework.data.repository.CrudRepository

interface ChampionshipRepository : CrudRepository<Championship, Int> {
    fun findByName(name: String): Championship?
}