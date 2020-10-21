package br.com.ais.pontoeletronico.service.dto

import java.io.Serializable
import java.time.LocalTime

/**
 * A DTO for the [br.com.ais.pontoeletronico.domain.Project] entity.
 */
data class ProjectDTO(

    var id: Long? = null,

    var name: String? = null,

    var allocatedHours: LocalTime? = null,

    var manager: Long? = null,

    var description: String? = null,

    var timesheets: MutableSet<TimesheetDTO>? = mutableSetOf(),

    var userId: Long? = null

) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectDTO) return false
        return id != null && id == other.id
    }

    override fun hashCode() = 31
}
