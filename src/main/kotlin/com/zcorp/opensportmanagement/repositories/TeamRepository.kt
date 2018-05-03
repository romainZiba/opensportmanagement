package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.*
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface TeamRepository : JpaRepository<Team, Int>, TeamDAO

interface TeamDAO {
    fun findByIds(names: List<Int>): List<Team>
    fun getTeamMembers(teamId: Int): List<TeamMember>
    fun getStadiums(teamId: Int): List<Stadium>
    fun getSeasons(teamId: Int): List<Season>
    fun getEvents(teamId: Int): List<AbstractEvent>
    fun getOpponents(teamId: Int): List<Opponent>
}

class TeamRepositoryImpl(@PersistenceContext private val em: EntityManager): TeamDAO {
    override fun findByIds(ids: List<Int>): MutableList<Team> {
        val q = em.createQuery("SELECT team FROM Team team WHERE team.id IN :ids", Team::class.java)
        q.setParameter("ids", ids)
        return q.resultList
    }

    override fun getTeamMembers(teamId: Int): List<TeamMember> {
        val q = em.createQuery(
                "SELECT member FROM TeamMember member WHERE member.team.id = :teamId", TeamMember::class.java)
        q.setParameter("teamId", teamId)
        return q.resultList
    }

    override fun getStadiums(teamId: Int): List<Stadium> {
        val q = em.createQuery(
                "SELECT stadiumId FROM Stadium stadiumId WHERE stadiumId.team.id = :teamId", Stadium::class.java)
        q.setParameter("teamId", teamId)
        return q.resultList
    }

    override fun getSeasons(teamId: Int): List<Season> {
        val q = em.createQuery(
                "SELECT season FROM Season season WHERE season.team.id = :teamId", Season::class.java)
        q.setParameter("teamId", teamId)
        return q.resultList
    }

    override fun getEvents(teamId: Int): List<AbstractEvent> {
        val q = em.createQuery(
                "SELECT event FROM AbstractEvent event WHERE event.team.id = :teamId", AbstractEvent::class.java)
        q.setParameter("teamId", teamId)
        return q.resultList
    }

    override fun getOpponents(teamId: Int): List<Opponent> {
        val q = em.createQuery(
                "SELECT opponent FROM Opponent opponent WHERE opponent.team.id = :teamId", Opponent::class.java)
        q.setParameter("teamId", teamId)
        return q.resultList
    }
}