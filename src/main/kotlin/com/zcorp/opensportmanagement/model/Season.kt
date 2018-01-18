package com.zcorp.opensportmanagement.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "season")
data class Season(val name: String,
                  @Convert(converter = LocalDateAttributeConverter::class) val fromDate: LocalDate,
                  @Convert(converter = LocalDateAttributeConverter::class) val toDate: LocalDate,
                  @Enumerated(EnumType.STRING) val status: Status,
                  @Id @GeneratedValue val id: Int = -1)

enum class Status {
    CURRENT, CLOSED
}