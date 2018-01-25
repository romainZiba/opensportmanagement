package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.TeamMember
import org.springframework.data.repository.CrudRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface TeamMemberRepository : CrudRepository<TeamMember, Int> {
    fun findByUsername(username: String): TeamMember?
}

class TeamMemberRepositoryImpl(@PersistenceContext val em: EntityManager) {
    fun findByUsername(username: String): TeamMember? {
        var q = em.createQuery("SELECT teamMember from TeamMember teamMember WHERE teamMember.user.username = :name", TeamMember::class.java)
        q.setParameter("name", username)
        return q.singleResult
    }
}