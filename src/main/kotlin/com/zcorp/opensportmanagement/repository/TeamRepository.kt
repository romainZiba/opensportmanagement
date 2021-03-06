package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.model.TeamMember
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.time.LocalDateTime

@RepositoryRestResource(exported = false)
interface TeamRepository : JpaRepository<Team, Int> {
    fun findByIdIn(ids: List<Int>): List<Team>

    @Query("SELECT m FROM TeamMember m WHERE m.team.id = :teamId")
    fun getTeamMembers(teamId: Int): List<TeamMember>

    @Query("SELECT m FROM TeamMember m WHERE m.id = :memberId AND m.team.id = :teamId")
    fun getTeamMember(teamId: Int, memberId: Int): TeamMember?

    @Query("SELECT s FROM Place s WHERE s.team.id = :teamId")
    fun getPlaces(teamId: Int): List<Place>

    @Query("SELECT s FROM Season s WHERE s.team.id = :teamId")
    fun getSeasons(teamId: Int): List<Season>

    @Query("SELECT e FROM AbstractEvent e WHERE e.team.id = :teamId ORDER BY e.fromDateTime")
    fun getEvents(teamId: Int, pageable: Pageable): Page<AbstractEvent>

    @Query("SELECT e " +
            " FROM AbstractEvent e " +
            " WHERE e.team.id = :teamId " +
            " AND :date <= e.fromDateTime " +
            " ORDER BY e.fromDateTime")
    fun getEvents(teamId: Int, pageable: Pageable, date: LocalDateTime): Page<AbstractEvent>

    @Query("SELECT o FROM Opponent o WHERE o.team.id = :teamId")
    fun getOpponents(teamId: Int): List<Opponent>

    @Query("SELECT m FROM TeamMember m WHERE m.account.username = :name AND m.team.id = :teamId")
    fun getTeamMemberByUserName(teamId: Int, name: String): TeamMember?
}