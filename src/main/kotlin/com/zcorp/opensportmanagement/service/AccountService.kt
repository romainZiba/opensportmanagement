package com.zcorp.opensportmanagement.service

import com.zcorp.opensportmanagement.dto.AccountDto
import com.zcorp.opensportmanagement.dto.AccountUpdateDto
import com.zcorp.opensportmanagement.model.TeamMember
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.repository.TeamRepository
import com.zcorp.opensportmanagement.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class AccountService @Autowired constructor(
    private val teamRepository: TeamRepository,
    private val accountRepository: AccountRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    @Transactional
    open fun findByUsername(username: String): AccountDto? {
        return accountRepository.findByUsername(username)?.toDto()
    }

    @Transactional
    open fun joinTeam(userId: String, teamId: Int): AccountDto {
        val user = accountRepository.findByUsername(userId) ?: throw NotFoundException("Account $userId does not exist")
        val team = teamRepository.findById(teamId)
                .orElseThrow { NotFoundException("Team $teamId does not exist") }
        val member = TeamMember(mutableSetOf(TeamMember.Role.PLAYER), team)
        user.addTeamMember(member)
        return accountRepository.save(user).toDto()
    }

    // TODO: leave team
    // TODO: delete account

    @Transactional
    open fun createUser(account: Account): AccountDto {
        account.password = bCryptPasswordEncoder.encode(account.password)
        return accountRepository.save(account).toDto()
    }

    @Transactional
    open fun updateUserProfile(dto: AccountUpdateDto, username: String): AccountDto {
        val user = accountRepository.findByUsername(username) ?: throw NotFoundException("Account $username does not exist")
        user.firstName = dto.firstName
        user.lastName = dto.lastName
        user.phoneNumber = dto.phoneNumber
        user.email = dto.email
        return accountRepository.save(user).toDto()
    }

    @Transactional
    open fun getTeamsAndRoles(username: String): Set<TeamMember>? {
        return accountRepository.findByUsername(username)?.getMemberOf()
    }
}