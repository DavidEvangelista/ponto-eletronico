package br.com.ais.pontoeletronico.web.rest

import br.com.ais.pontoeletronico.PontoeletronicoServiceApp
import br.com.ais.pontoeletronico.domain.Project
import br.com.ais.pontoeletronico.domain.Timesheet
import br.com.ais.pontoeletronico.repository.ProjectRepository
import br.com.ais.pontoeletronico.service.ProjectService
import br.com.ais.pontoeletronico.service.mapper.ProjectMapper
import br.com.ais.pontoeletronico.web.rest.errors.ExceptionTranslator
import java.time.LocalTime
import javax.persistence.EntityManager
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator

/**
 * Integration tests for the [ProjectResource] REST controller.
 *
 * @see ProjectResource
 */
@SpringBootTest(classes = [PontoeletronicoServiceApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ProjectResourceIT {

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var projectMapper: ProjectMapper

    @Autowired
    private lateinit var projectService: ProjectService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restProjectMockMvc: MockMvc

    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val projectResource = ProjectResource(projectService)
         this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        project = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProject() {
        val databaseSizeBeforeCreate = projectRepository.findAll().size

        // Create the Project
        val projectDTO = projectMapper.toDto(project)
        restProjectMockMvc.perform(
            post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(projectDTO))
        ).andExpect(status().isCreated)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1)
        val testProject = projectList[projectList.size - 1]
        assertThat(testProject.name).isEqualTo(DEFAULT_NAME)
        assertThat(testProject.allocatedHours).isEqualTo(DEFAULT_ALLOCATED_HOURS)
        assertThat(testProject.manager).isEqualTo(DEFAULT_MANAGER)
        assertThat(testProject.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    fun createProjectWithExistingId() {
        val databaseSizeBeforeCreate = projectRepository.findAll().size

        // Create the Project with an existing ID
        project.id = 1L
        val projectDTO = projectMapper.toDto(project)

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc.perform(
            post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(projectDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProjects() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        // Get all the projectList
        restProjectMockMvc.perform(get("/api/projects?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].allocatedHours").value(hasItem(DEFAULT_ALLOCATED_HOURS)))
            .andExpect(jsonPath("$.[*].manager").value(hasItem(DEFAULT_MANAGER?.toInt())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProject() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        val id = project.id
        assertNotNull(id)

        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.allocatedHours").value(DEFAULT_ALLOCATED_HOURS))
            .andExpect(jsonPath("$.manager").value(DEFAULT_MANAGER?.toInt()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION)) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProject() {
        // Get the project
        restProjectMockMvc.perform(get("/api/projects/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateProject() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        val databaseSizeBeforeUpdate = projectRepository.findAll().size

        // Update the project
        val id = project.id
        assertNotNull(id)
        val updatedProject = projectRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject)
        updatedProject.name = UPDATED_NAME
        updatedProject.allocatedHours = UPDATED_ALLOCATED_HOURS
        updatedProject.manager = UPDATED_MANAGER
        updatedProject.description = UPDATED_DESCRIPTION
        val projectDTO = projectMapper.toDto(updatedProject)

        restProjectMockMvc.perform(
            put("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(projectDTO))
        ).andExpect(status().isOk)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
        val testProject = projectList[projectList.size - 1]
        assertThat(testProject.name).isEqualTo(UPDATED_NAME)
        assertThat(testProject.allocatedHours).isEqualTo(UPDATED_ALLOCATED_HOURS)
        assertThat(testProject.manager).isEqualTo(UPDATED_MANAGER)
        assertThat(testProject.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun updateNonExistingProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size

        // Create the Project
        val projectDTO = projectMapper.toDto(project)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            put("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(projectDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProject() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        val databaseSizeBeforeDelete = projectRepository.findAll().size

        // Delete the project
        restProjectMockMvc.perform(
            delete("/api/projects/{id}", project.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val DEFAULT_ALLOCATED_HOURS: LocalTime = LocalTime.of(8, 0)
        private val UPDATED_ALLOCATED_HOURS: LocalTime = LocalTime.of(8, 0)

        private const val DEFAULT_MANAGER: Long = 3L
        private const val UPDATED_MANAGER: Long = 2L

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private const val TIMESHEET_ID: Long = 1L

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Project {
            val timesheets = mutableSetOf(Timesheet(TIMESHEET_ID))

            val project = Project(
                name = DEFAULT_NAME,
                allocatedHours = DEFAULT_ALLOCATED_HOURS,
                manager = DEFAULT_MANAGER,
                description = DEFAULT_DESCRIPTION,
                timesheets = timesheets
            )

            return project
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Project {
            val project = Project(
                name = UPDATED_NAME,
                allocatedHours = UPDATED_ALLOCATED_HOURS,
                manager = UPDATED_MANAGER,
                description = UPDATED_DESCRIPTION
            )

            return project
        }
    }
}
