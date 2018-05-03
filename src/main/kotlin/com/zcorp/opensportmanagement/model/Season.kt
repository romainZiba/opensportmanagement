package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.LocalDateAttributeConverter
import com.zcorp.opensportmanagement.dto.SeasonDto
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(
        name = "season",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Season(@Column(name = "name") val name: String,
                  @Convert(converter = LocalDateAttributeConverter::class) val fromDate: LocalDate,
                  @Convert(converter = LocalDateAttributeConverter::class) val toDate: LocalDate,
                  @Enumerated(EnumType.STRING) val status: Status,
                  @ManyToOne @JoinColumn(name = "team_id") val team: Team,
                  @Id @GeneratedValue val id: Int = -1) {

    fun toDto(): SeasonDto {
        return SeasonDto(name, fromDate, toDate, status)
    }

    enum class Status {
        CURRENT, PREVIOUS, NEXT
    }
}