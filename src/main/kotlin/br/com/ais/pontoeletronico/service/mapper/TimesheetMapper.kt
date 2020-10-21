package br.com.ais.pontoeletronico.service.mapper

import br.com.ais.pontoeletronico.domain.Timesheet
import br.com.ais.pontoeletronico.service.dto.TimesheetDTO
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

/**
 * Mapper for the entity [Timesheet] and its DTO [TimesheetDTO].
 */
@Mapper(componentModel = "spring", uses = [], unmappedTargetPolicy = ReportingPolicy.ERROR)
interface TimesheetMapper :
    EntityMapper<TimesheetDTO, Timesheet> {

    override fun toEntity(timesheetDTO: TimesheetDTO): Timesheet
}
