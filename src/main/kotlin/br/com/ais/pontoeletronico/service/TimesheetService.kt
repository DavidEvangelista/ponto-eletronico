package br.com.ais.pontoeletronico.service
import br.com.ais.pontoeletronico.service.dto.TimesheetDTO
import java.util.Optional

/**
 * Service Interface for managing [br.com.ais.pontoeletronico.domain.Timesheet].
 */
interface TimesheetService {

    /**
     * Save a timesheet.
     *
     * @param timesheetDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(timesheetDTO: TimesheetDTO): TimesheetDTO

    /**
     * Get all the timesheets.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<TimesheetDTO>

    /**
     * Get the "id" timesheet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<TimesheetDTO>

    /**
     * Delete the "id" timesheet.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
