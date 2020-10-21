package br.com.ais.pontoeletronico.service.dto

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import javax.validation.constraints.NotNull

/**
 * A DTO for the [br.com.ais.pontoeletronico.domain.Timesheet] entity.
 */
data class TimesheetDTO(

    var id: Long? = null,

    @get:NotNull
    var weekday: LocalDate = LocalDate.now(),

    @get:NotNull
    var checkin: LocalTime = LocalTime.of(9, 0),

    @get:NotNull
    var goLunch: LocalTime = LocalTime.of(12, 0),

    @get:NotNull
    var backLunch: LocalTime = LocalTime.of(13, 0),

    @get:NotNull
    var checkout: LocalTime = LocalTime.of(18, 0),

    var total: Long? = null,

    var userId: Long? = null,

    var project: ProjectDTO? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimesheetDTO) return false
        return id != null && id == other.id
    }

    override fun hashCode() = 31
}
