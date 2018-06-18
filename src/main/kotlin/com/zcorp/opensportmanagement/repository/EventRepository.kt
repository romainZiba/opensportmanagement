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
            " LEFT JOIN ev.presentMembers present " +
            " LEFT JOIN ev.absentMembers absent " +
            " LEFT JOIN ev.waitingMembers waiting " +
            " WHERE ev.id = :eventId " +
            "      AND ev.team.id = m.team.id " +
            "      AND (present IS NULL OR m.id != present.id) " +
            "      AND (absent IS NULL OR m.id != absent.id) " +
            "      AND (waiting IS NULL OR m.id != waiting.id)")
    fun getMembersThatHaveNotResponded(@Param("eventId") eventId: Int): List<TeamMember>
}