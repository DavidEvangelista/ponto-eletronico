package br.com.ais.pontoeletronico.repository

import br.com.ais.pontoeletronico.domain.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Project] entity.
 */
@Suppress("unused")
@Repository
interface ProjectRepository : JpaRepository<Project, Long>
