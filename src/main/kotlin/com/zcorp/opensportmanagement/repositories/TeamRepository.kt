package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TeamRepository : JpaRepository<Team, Int>, TeamDAO

interface TeamDAO {
    @Query("SELECT t FROM Team t WHERE t.id IN :ids")
    fun findByIds(ids: List<Int>): List<Team>

    @Query("SELECT m FROM TeamMember m WHERE m.team.id = :teamId")
    fun getTeamMembers(teamId: Int): List<TeamMember>

    @Query("SELECT m FROM TeamMember m WHERE m.id = :memberId AND m.team.id = :teamId")
    fun getTeamMember(teamId: Int, memberId: Int): TeamMember?

    @Query("SELECT s FROM Stadium s WHERE s.team.id = :teamId")
    fun getStadiums(teamId: Int): List<Stadium>

    @Query("SELECT s FROM Season s WHERE s.team.id = :teamId")
    fun getSeasons(teamId: Int): List<Season>

    @Query("SELECT e FROM AbstractEvent e WHERE e.team.id = :teamId ORDER BY e.fromDateTime")
    fun getEvents(teamId: Int, pageable: Pageable): Page<AbstractEvent>

    @Query("SELECT o FROM Opponent o WHERE o.team.id = :teamId")
    fun getOpponents(teamId: Int): List<Opponent>

    @Query("SELECT m FROM TeamMember m WHERE m.user.username = :name AND m.team.id = :teamId")
    fun getTeamMemberByUserName(teamId: Int, name: String): TeamMember?
}