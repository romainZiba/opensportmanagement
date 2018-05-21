package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.LocalDateAttributeConverter
import com.zcorp.opensportmanagement.dto.SeasonDto
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
        name = "season",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Season(
    @Column(name = "name") val name: String,
    @Convert(converter = LocalDateAttributeConverter::class) val fromDate: LocalDate,
    @Convert(converter = LocalDateAttributeConverter::class) val toDate: LocalDate,
    @Enumerated(EnumType.STRING) val status: Status,
    @ManyToOne @JoinColumn(name = "team_id") val team: Team,
    @Id @GeneratedValue val id: Int = -1
) {

    fun toDto(): SeasonDto {
        return SeasonDto(name, fromDate, toDate, status, team.id, id)
    }

    enum class Status {
        CURRENT, PREVIOUS, NEXT
    }
}