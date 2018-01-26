package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.TeamMember
import org.springframework.data.repository.CrudRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface TeamMemberRepository : CrudRepository<TeamMember, Int> {
    fun findByUsername(username: String, teamId: Int): TeamMember?
}

class TeamMemberRepositoryImpl(@PersistenceContext val em: EntityManager) {
    fun findByUsername(username: String, teamId: Int): TeamMember? {
        var q = em.createQuery("SELECT teamMember FROM TeamMember teamMember WHERE teamMember.user.username = :username AND teamMember.team.id = :teamId", TeamMember::class.java)
        q.setParameter("username", username)
        q.setParameter("teamId", teamId)
        return q.singleResult
    }
}