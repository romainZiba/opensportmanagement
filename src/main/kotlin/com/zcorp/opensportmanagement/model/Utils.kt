package com.zcorp.opensportmanagement.model

import java.sql.Date
import java.time.LocalDate
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * Convert Java 8 LocalDate into SQL DATE.
 * This converter is needed because JPA still not manage Java 8 DateTime API.
 */
@Converter(autoApply = true)
class LocalDateAttributeConverter : AttributeConverter<LocalDate, Date> {

    override fun convertToDatabaseColumn(locDate: LocalDate?): Date? {
        return if (locDate == null) null else Date.valueOf(locDate)
    }

    override fun convertToEntityAttribute(sqlDate: Date?): LocalDate? {
        return sqlDate?.toLocalDate()
    }
}