package br.com.ais.pontoeletronico.domain

import br.com.ais.pontoeletronico.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProjectTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Project::class)
        val project1 = Project()
        project1.id = 1L
        val project2 = Project()
        project2.id = project1.id
        assertThat(project1).isEqualTo(project2)
        project2.id = 2L
        assertThat(project1).isNotEqualTo(project2)
        project1.id = null
        assertThat(project1).isNotEqualTo(project2)
    }
}
