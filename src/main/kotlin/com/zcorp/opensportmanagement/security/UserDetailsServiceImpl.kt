package com.zcorp.opensportmanagement.security

import com.zcorp.opensportmanagement.repository.UserRepository
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
open class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
        return User(user.username,
                user.password,
                user.getMemberOf().mapTo(LinkedList<GrantedAuthority>()) { OpenGrantedAuthority(it.team.id!!, it.roles) })
    }
}