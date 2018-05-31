package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Championship
import org.springframework.data.jpa.repository.JpaRepository
import javax.transaction.Transactional

@Transactional(Transactional.TxType.MANDATORY)
interface ChampionshipRepository : JpaRepository<Championship, Int>