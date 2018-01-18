package com.zcorp.opensportmanagement.services

import com.zcorp.opensportmanagement.model.Championship
import org.springframework.data.repository.CrudRepository

interface ChampionshipRepository : CrudRepository<Championship, Int>