package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface TeamRepository : JpaRepository<Team, Int>, TeamDAO {
    fun findByName(name: String): Team?
}

interface TeamDAO {
    fun findByNames(names: List<String>): List<Team>
}

class TeamRepositoryImpl(@PersistenceContext val em: EntityManager) {
    fun findByNames(names: List<String>): MutableList<Team> {
        var q = em.createQuery("SELECT team from Team team WHERE team.name IN :names", Team::class.java)
        q.setParameter("names", names)
        return q.resultList
    }

}