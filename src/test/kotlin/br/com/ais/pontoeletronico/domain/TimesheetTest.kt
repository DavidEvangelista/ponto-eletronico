package br.com.ais.pontoeletronico.domain

import br.com.ais.pontoeletronico.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TimesheetTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Timesheet::class)
        val timesheet1 = Timesheet()
        timesheet1.id = 1L
        val timesheet2 = Timesheet()
        timesheet2.id = timesheet1.id
        assertThat(timesheet1).isEqualTo(timesheet2)
        timesheet2.id = 2L
        assertThat(timesheet1).isNotEqualTo(timesheet2)
        timesheet1.id = null
        assertThat(timesheet1).isNotEqualTo(timesheet2)
    }
}
