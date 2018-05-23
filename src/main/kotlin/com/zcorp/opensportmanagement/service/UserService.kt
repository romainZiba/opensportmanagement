package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.dto.UserUpdateDto
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class UserService @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    @Transactional
    open fun findByUsername(username: String): UserDto? {
        return userRepository.findByUsername(username)?.toDto()
    }

    @Transactional
    open fun joinTeam(userId: String, teamId: Int): UserDto {
        val user = userRepository.findByUsername(userId) ?: throw NotFoundException("User $userId does not exist")
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        val member = TeamMember(mutableSetOf(TeamMember.Role.PLAYER), team)
        user.addTeamMember(member)
        return userRepository.save(user).toDto()
    }

    @Transactional
    open fun createUser(user: User): UserDto {
        user.password = bCryptPasswordEncoder.encode(user.password)
        return userRepository.save(user).toDto()
    }

    @Transactional
    open fun updateUserProfile(dto: UserUpdateDto, username: String): UserDto {
        val user = userRepository.findByUsername(username) ?: throw NotFoundException("User $username does not exist")
        user.firstName = dto.firstName
        user.lastName = dto.lastName
        user.phoneNumber = dto.phoneNumber
        user.email = dto.email
        return userRepository.save(user).toDto()
    }
}