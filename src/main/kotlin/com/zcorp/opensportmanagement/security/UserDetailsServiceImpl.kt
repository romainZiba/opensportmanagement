package com.zcorp.opensportmanagement.security

import com.zcorp.opensportmanagement.repository.AccountRepository
import com.zcorp.opensportmanagement.repository.TeamMemberRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.LinkedList

@Service
@Qualifier("osm_user_details")
open class UserDetailsServiceImpl(
    private val accountRepository: AccountRepository,
    private val teamMemberRepository: TeamMemberRepository
) : UserDetailsService {
    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        var user = accountRepository.findByUsername(username)
        if (user == null) {
            user = accountRepository.findByEmailIgnoreCase(username) ?: throw UsernameNotFoundException(username)
        }
        return User(user.username,
                user.password,
                teamMemberRepository.findMemberOfByUsername(user.username)
                        .mapTo(LinkedList<GrantedAuthority>()) { OpenGrantedAuthority(it.team.id!!, it.roles) })
    }
}