package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.AbstractEvent
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface EventRepository : JpaRepository<AbstractEvent, Int>, EventDAO

interface EventDAO {
    fun getEventById(id: Int): AbstractEvent
}

class EventRepositoryImpl(@PersistenceContext private val em: EntityManager): EventDAO {
    override fun getEventById(id: Int): AbstractEvent {
        return em.createQuery("SELECT event FROM AbstractEvent event WHERE event.id = :id", AbstractEvent::class.java)
                .setParameter("id", id)
                .singleResult
    }

}