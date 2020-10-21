package br.com.ais.pontoeletronico.service
import br.com.ais.pontoeletronico.service.dto.ProjectDTO
import java.util.Optional

/**
 * Service Interface for managing [br.com.ais.pontoeletronico.domain.Project].
 */
interface ProjectService {

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(projectDTO: ProjectDTO): ProjectDTO

    /**
     * Get all the projects.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<ProjectDTO>

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<ProjectDTO>

    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
