package com.zcorp.opensportmanagement.services

import com.zcorp.opensportmanagement.model.Opponent
import org.springframework.data.repository.CrudRepository

interface OpponentRepository : CrudRepository<Opponent, Int>