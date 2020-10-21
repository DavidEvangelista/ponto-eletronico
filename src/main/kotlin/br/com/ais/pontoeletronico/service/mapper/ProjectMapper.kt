package br.com.ais.pontoeletronico.service.mapper

import br.com.ais.pontoeletronico.domain.Project
import br.com.ais.pontoeletronico.service.dto.ProjectDTO
import org.mapstruct.Mapper

/**
 * Mapper for the entity [Project] and its DTO [ProjectDTO].
 */
@Mapper(componentModel = "spring", uses = [])
interface ProjectMapper :
    EntityMapper<ProjectDTO, Project> {

    override fun toEntity(projectDTO: ProjectDTO): Project
}
