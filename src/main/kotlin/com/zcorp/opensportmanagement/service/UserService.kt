package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.EventDto
import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.EventRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import com.zcorp.opensportmanagement.rest.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
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
        val user = userRepository.findByUsername(userId) ?: throw NotFoundException("User $userId not found")
        val team = teamRepository.getOne(teamId)
        val member = TeamMember(mutableSetOf(TeamMember.Role.PLAYER), team)
        user.addTeamMember(member)
        userRepository.save(user)
    }

    @Transactional
    open fun participate(username: String, eventId: Int, present: Boolean): EventDto {
        val event = eventRepository.getOne(eventId)
        val teamMember = teamMemberRepository.findByUsername(username, event.team.id) ?: throw NotFoundException("Team member $username does not exist")
        event.participate(teamMember, present)
        return eventRepository.save(event).toDto()
    }

    @Transactional
    open fun createUser(user: User): UserDto {
        user.password = bCryptPasswordEncoder.encode(user.password)
        return userRepository.save(user).toDto()
    }
}