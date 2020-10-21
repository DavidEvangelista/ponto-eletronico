package br.com.ais.pontoeletronico.repository

import br.com.ais.pontoeletronico.domain.Timesheet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data  repository for the [Timesheet] entity.
 */
@Suppress("unused")
@Repository
interface TimesheetRepository : JpaRepository<Timesheet, Long>
