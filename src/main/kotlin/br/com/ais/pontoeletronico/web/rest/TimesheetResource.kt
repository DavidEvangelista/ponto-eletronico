package br.com.ais.pontoeletronico.web.rest

import br.com.ais.pontoeletronico.service.TimesheetService
import br.com.ais.pontoeletronico.service.dto.TimesheetDTO
import br.com.ais.pontoeletronico.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "pontoeletronicoServiceTimesheet"
/**
 * REST controller for managing [br.com.ais.pontoeletronico.domain.Timesheet].
 */
@RestController
@RequestMapping("/api")
class TimesheetResource(
    private val timesheetService: TimesheetService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /timesheets` : Create a new timesheet.
     *
     * @param timesheetDTO the timesheetDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new timesheetDTO, or with status `400 (Bad Request)` if the timesheet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/timesheets")
    fun createTimesheet(@RequestBody timesheetDTO: TimesheetDTO): ResponseEntity<TimesheetDTO> {
        log.debug("REST request to save Timesheet : $timesheetDTO")
        if (timesheetDTO.id != null) {
            throw BadRequestAlertException(
                "A new timesheet cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = timesheetService.save(timesheetDTO)
        return ResponseEntity.created(URI("/api/timesheets/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /timesheets` : Updates an existing timesheet.
     *
     * @param timesheetDTO the timesheetDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated timesheetDTO,
     * or with status `400 (Bad Request)` if the timesheetDTO is not valid,
     * or with status `500 (Internal Server Error)` if the timesheetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/timesheets")
    fun updateTimesheet(@RequestBody timesheetDTO: TimesheetDTO): ResponseEntity<TimesheetDTO> {
        log.debug("REST request to update Timesheet : $timesheetDTO")
        if (timesheetDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = timesheetService.save(timesheetDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                    timesheetDTO.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /timesheets` : get all the timesheets.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of timesheets in body.
     */
    @GetMapping("/timesheets")
    fun getAllTimesheets(): MutableList<TimesheetDTO> {
        log.debug("REST request to get all Timesheets")

        return timesheetService.findAll()
            }

    /**
     * `GET  /timesheets/:id` : get the "id" timesheet.
     *
     * @param id the id of the timesheetDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the timesheetDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/timesheets/{id}")
    fun getTimesheet(@PathVariable id: Long): ResponseEntity<TimesheetDTO> {
        log.debug("REST request to get Timesheet : $id")
        val timesheetDTO = timesheetService.findOne(id)
        return ResponseUtil.wrapOrNotFound(timesheetDTO)
    }
    /**
     *  `DELETE  /timesheets/:id` : delete the "id" timesheet.
     *
     * @param id the id of the timesheetDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/timesheets/{id}")
    fun deleteTimesheet(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Timesheet : $id")

        timesheetService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
