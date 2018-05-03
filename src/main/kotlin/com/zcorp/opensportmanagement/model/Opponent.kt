package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.OpponentDto
import javax.persistence.*

@Entity
@Table(name = "opponent", uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Opponent(@Column(name = "name") val name: String,
                    val phoneNumber: String,
                    val email: String,
                    var imgUrl: String = "",
                    @ManyToOne @JoinColumn(name = "team_id") val team: Team,
                    @Id @GeneratedValue val id: Int = -1) {
    fun toDto(): OpponentDto {
        return OpponentDto(name, phoneNumber, email, imgUrl)
    }
}