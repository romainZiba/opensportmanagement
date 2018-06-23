package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.AbstractEvent
import com.zcorp.opensportmanagement.model.TeamMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface EventRepository : JpaRepository<AbstractEvent, Int> {
    fun getEventById(id: Int): AbstractEvent?
    fun getEventsByNotifiedFalseAndFromDateTimeBefore(fromDate: LocalDateTime): List<AbstractEvent>
    @Query("SELECT m FROM TeamMember m, AbstractEvent ev " +
            " WHERE ev.id = :eventId " +
            " AND m.team.id = ev.team.id " +
            " AND m.id NOT IN (SELECT DISTINCT(response.teamMember.id) " +
            "                   FROM TeamMember m1, AbstractEvent ev1 " +
            "                   LEFT JOIN ev1.membersResponse response " +
            "                   WHERE ev1.id = :eventId " +
            "                   AND ev1.team.id = m1.team.id)")
    fun getMembersThatHaveNotResponded(@Param("eventId") eventId: Int): List<TeamMember>
}