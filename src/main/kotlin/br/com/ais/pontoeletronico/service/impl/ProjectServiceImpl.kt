package br.com.ais.pontoeletronico.service.impl

import br.com.ais.pontoeletronico.domain.Project
import br.com.ais.pontoeletronico.repository.ProjectRepository
import br.com.ais.pontoeletronico.service.ProjectService
import br.com.ais.pontoeletronico.service.TimesheetService
import br.com.ais.pontoeletronico.service.dto.ProjectDTO
import br.com.ais.pontoeletronico.service.dto.TimesheetDTO
import br.com.ais.pontoeletronico.service.erros.AllocationHoursBusinessException
import br.com.ais.pontoeletronico.service.mapper.ProjectMapper
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Project].
 */
@Service
@Transactional
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository,
    private val projectMapper: ProjectMapper,
    private val timesheetService: TimesheetService
) : ProjectService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(projectDTO: ProjectDTO): ProjectDTO {
        log.debug("Request to save Project : $projectDTO")

        checkHoursAvailableOnTimesheet(projectDTO)

        var project = projectMapper.toEntity(projectDTO)
        project = projectRepository.save(project)

        updateTimesheetAddProject(project)

        return projectMapper.toDto(project)
    }

    @Transactional(readOnly = true)
    override fun findAll(): MutableList<ProjectDTO> {
        log.debug("Request to get all Projects")
        return projectRepository.findAll()
            .mapTo(mutableListOf(), projectMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<ProjectDTO> {
        log.debug("Request to get Project : $id")
        return projectRepository.findById(id)
            .map(projectMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Project : $id")

        projectRepository.deleteById(id)
    }

    private fun checkHoursAvailableOnTimesheet(projectDTO: ProjectDTO) {
        var totalHoursAvailable: Long = 0

        projectDTO.timesheets?.forEach { t ->
            t.id?.let {
                val timesheet: TimesheetDTO = timesheetService.findOne(it).get()
                totalHoursAvailable += timesheet.total!!
                if (Objects.nonNull(timesheet.project)) {
                    totalHoursAvailable -= localTimeToMillis(timesheet.project?.allocatedHours)
                }
            }
        }

        var allocatedHours: Long = localTimeToMillis(projectDTO.allocatedHours)

        if (allocatedHours > totalHoursAvailable) {
            throw AllocationHoursBusinessException("Não é possível alocar horas além das horas disponíveis nas planilhas de horários!")
        }
    }

    private fun localTimeToMillis(localTime: LocalTime?): Long {
        if (localTime != null) {
            var hour: String = localTime.format(DateTimeFormatter.ofPattern("'PT'H'H'mm'M'"))
            return Duration.parse(hour).toMillis()
        }
        return 0L
    }

    private fun updateTimesheetAddProject(project: Project) {
        project.timesheets?.forEach { t ->
            t.id?.let {
                val timesheet: TimesheetDTO = timesheetService.findOne(it).get()
                timesheet.project = ProjectDTO()
                timesheet.project!!.id = project.id
                timesheetService.save(timesheet)
            }
        }
    }
}
