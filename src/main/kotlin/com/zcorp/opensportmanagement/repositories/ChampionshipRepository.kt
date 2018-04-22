package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Championship
import org.springframework.data.jpa.repository.JpaRepository

interface ChampionshipRepository : JpaRepository<Championship, Int> {
    fun findByName(name: String): Championship?
}