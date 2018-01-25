package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface TeamRepository : JpaRepository<Team, Int>, TeamDAO

interface TeamDAO {
    fun findByIds(names: List<Int>): List<Team>
}

class TeamRepositoryImpl(@PersistenceContext val em: EntityManager) {
    fun findByIds(ids: List<Int>): MutableList<Team> {
        var q = em.createQuery("SELECT team from Team team WHERE team.id IN :ids", Team::class.java)
        q.setParameter("ids", ids)
        return q.resultList
    }
}