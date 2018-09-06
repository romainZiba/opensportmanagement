package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.TeamMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface TeamMemberRepository : JpaRepository<TeamMember, Int> {
    @Query("SELECT teamMember " +
            " FROM TeamMember teamMember " +
            " WHERE teamMember.account.username = :username " +
            " AND teamMember.team.id = :teamId")
    fun findByUsername(
        @Param("username") username: String,
        @Param("teamId") teamId: Int
    ): TeamMember?

    @Query("SELECT teamMember FROM TeamMember teamMember " +
            " WHERE teamMember.account.username = :username ")
    fun findMemberOfByUsername(
        @Param("username") username: String
    ): List<TeamMember>
}