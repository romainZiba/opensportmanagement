package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
@Table(name = "team_member",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("USER_USERNAME", "TEAM_ID")))])
data class TeamMember(
        @ManyToOne
        @JsonBackReference(value = "memberOf")
        @JoinColumn(name = "USER_USERNAME", nullable = false)
        val user: User,
        @ElementCollection val roles: MutableSet<Role>,
        @ManyToOne @JsonBackReference @JoinColumn(name = "TEAM_ID") val team: Team) {

    @Id
    @GeneratedValue
    val id: Int = -1

    var licenseNumber: String? = null

    override fun toString(): String {
        return "TeamMember(user='$user', roles='$roles')"
    }

    enum class Role {
        PLAYER, COACH, ADMIN
    }
}