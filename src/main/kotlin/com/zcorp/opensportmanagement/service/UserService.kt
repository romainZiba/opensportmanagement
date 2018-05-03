package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.rest.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class UserService @Autowired constructor(private val teamRepository: TeamRepository,
                                              private val teamMemberRepository: TeamMemberRepository,
                                              private val eventRepository: EventRepository,
                                              private val userRepository: UserRepository,
                                              private val bCryptPasswordEncoder: BCryptPasswordEncoder) {


    @Transactional
    open fun findByUsername(username: String): UserDto? {
        return userRepository.findByUsername(username)?.toDto()
    }

    @Transactional
    open fun joinTeam(userId: String, teamId: Int) {
        val user = userRepository.findByUsername(userId)
        val optionalTeam = teamRepository.findById(teamId)
        if (user != null && optionalTeam.isPresent) {
            val team = optionalTeam.get()
            var member = TeamMember(mutableSetOf(TeamMember.Role.PLAYER), team)
            user.addTeamMember(member)
            userRepository.save(user)
        }
    }

    @Transactional
    open fun participate(username: String, eventId: Int, present: Boolean): EventDto {
        var event = eventRepository.getOne(eventId)
        val teamMember = teamMemberRepository.findByUsername(username, event.team.id) ?: throw EntityNotFoundException("Team member $username does not exist")
        event.parcipate(teamMember, present)
        return eventRepository.save(event).toDto()
    }

    @Transactional
    open fun createUser(user: User): UserDto {
        user.password = bCryptPasswordEncoder.encode(user.password)
        return userRepository.save(user).toDto()
    }
}