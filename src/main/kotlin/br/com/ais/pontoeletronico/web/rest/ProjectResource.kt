package br.com.ais.pontoeletronico.web.rest

import br.com.ais.pontoeletronico.service.ProjectService
import br.com.ais.pontoeletronico.service.dto.ProjectDTO
import br.com.ais.pontoeletronico.web.rest.errors.BadRequestAlertException
import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val ENTITY_NAME = "pontoeletronicoServiceProject"
/**
 * REST controller for managing [br.com.ais.pontoeletronico.domain.Project].
 */
@RestController
@RequestMapping("/api")
class ProjectResource(
    private val projectService: ProjectService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /projects` : Create a new project.
     *
     * @param projectDTO the projectDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new projectDTO, or with status `400 (Bad Request)` if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projects")
    fun createProject(@RequestBody projectDTO: ProjectDTO): ResponseEntity<ProjectDTO> {
        log.debug("REST request to save Project : $projectDTO")
        if (projectDTO.id != null) {
            throw BadRequestAlertException(
                "A new project cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = projectService.save(projectDTO)
        return ResponseEntity.created(URI("/api/projects/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /projects` : Updates an existing project.
     *
     * @param projectDTO the projectDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated projectDTO,
     * or with status `400 (Bad Request)` if the projectDTO is not valid,
     * or with status `500 (Internal Server Error)` if the projectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/projects")
    fun updateProject(@RequestBody projectDTO: ProjectDTO): ResponseEntity<ProjectDTO> {
        log.debug("REST request to update Project : $projectDTO")
        if (projectDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = projectService.save(projectDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, false, ENTITY_NAME,
                     projectDTO.id.toString()
                )
            )
            .body(result)
    }
    /**
     * `GET  /projects` : get all the projects.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of projects in body.
     */
    @GetMapping("/projects")
    fun getAllProjects(): MutableList<ProjectDTO> {
        log.debug("REST request to get all Projects")

        return projectService.findAll()
    }

    /**
     * `GET  /projects/:id` : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the projectDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/projects/{id}")
    fun getProject(@PathVariable id: Long): ResponseEntity<ProjectDTO> {
        log.debug("REST request to get Project : $id")
        val projectDTO = projectService.findOne(id)
        return ResponseUtil.wrapOrNotFound(projectDTO)
    }
    /**
     *  `DELETE  /projects/:id` : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/projects/{id}")
    fun deleteProject(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Project : $id")

        projectService.delete(id)
            return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build()
    }
}
