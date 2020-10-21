package br.com.ais.pontoeletronico.web.rest

import br.com.ais.pontoeletronico.PontoeletronicoServiceApp
import br.com.ais.pontoeletronico.domain.Timesheet
import br.com.ais.pontoeletronico.repository.TimesheetRepository
import br.com.ais.pontoeletronico.service.TimesheetService
import br.com.ais.pontoeletronico.service.mapper.TimesheetMapper
import br.com.ais.pontoeletronico.web.rest.errors.ExceptionTranslator
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
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
 * Integration tests for the [TimesheetResource] REST controller.
 *
 * @see TimesheetResource
 */
@SpringBootTest(classes = [PontoeletronicoServiceApp::class])
@AutoConfigureMockMvc
@WithMockUser
class TimesheetResourceIT {

    @Autowired
    private lateinit var timesheetRepository: TimesheetRepository

    @Autowired
    private lateinit var timesheetMapper: TimesheetMapper

    @Autowired
    private lateinit var timesheetService: TimesheetService

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

    private lateinit var restTimesheetMockMvc: MockMvc

    private lateinit var timesheet: Timesheet

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val timesheetResource = TimesheetResource(timesheetService)
         this.restTimesheetMockMvc = MockMvcBuilders.standaloneSetup(timesheetResource)
             .setCustomArgumentResolvers(pageableArgumentResolver)
             .setControllerAdvice(exceptionTranslator)
             .setConversionService(createFormattingConversionService())
             .setMessageConverters(jacksonMessageConverter)
             .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        timesheet = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTimesheet() {
        val databaseSizeBeforeCreate = timesheetRepository.findAll().size

        // Create the Timesheet
        val timesheetDTO = timesheetMapper.toDto(timesheet)
        restTimesheetMockMvc.perform(
            post("/api/timesheets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(timesheetDTO))
        ).andExpect(status().isCreated)

        // Validate the Timesheet in the database
        val timesheetList = timesheetRepository.findAll()
        assertThat(timesheetList).hasSize(databaseSizeBeforeCreate + 1)
        val testTimesheet = timesheetList[timesheetList.size - 1]
        assertThat(testTimesheet.weekday).isEqualTo(DEFAULT_WEEKDAY)
        assertThat(testTimesheet.goLunch).isEqualTo(DEFAULT_GO_LUNCH)
        assertThat(testTimesheet.backLunch).isEqualTo(DEFAULT_BACK_LUNCH)
        assertThat(testTimesheet.checkin).isEqualTo(DEFAULT_CHECKIN)
        assertThat(testTimesheet.checkout).isEqualTo(DEFAULT_CHECKOUT)
    }

    @Test
    @Transactional
    fun createTimesheetWithExistingId() {
        val databaseSizeBeforeCreate = timesheetRepository.findAll().size

        // Create the Timesheet with an existing ID
        timesheet.id = 1L
        val timesheetDTO = timesheetMapper.toDto(timesheet)

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimesheetMockMvc.perform(
            post("/api/timesheets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(timesheetDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Timesheet in the database
        val timesheetList = timesheetRepository.findAll()
        assertThat(timesheetList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllTimesheets() {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet)

        // Get all the timesheetList
        restTimesheetMockMvc.perform(get("/api/timesheets?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timesheet.id?.toInt())))
            .andExpect(jsonPath("$.[*].weekday").value(hasItem(DEFAULT_WEEKDAY.toString())))
            .andExpect(jsonPath("$.[*].goLunch").value(hasItem(DEFAULT_GO_LUNCH.toString())))
            .andExpect(jsonPath("$.[*].backLunch").value(hasItem(DEFAULT_BACK_LUNCH.toString())))
            .andExpect(jsonPath("$.[*].checkin").value(hasItem(DEFAULT_CHECKIN.toString())))
            .andExpect(jsonPath("$.[*].checkout").value(hasItem(DEFAULT_CHECKOUT.toString()))) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getTimesheet() {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet)

        val id = timesheet.id
        assertNotNull(id)

        // Get the timesheet
        restTimesheetMockMvc.perform(get("/api/timesheets/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timesheet.id?.toInt()))
            .andExpect(jsonPath("$.weekday").value(DEFAULT_WEEKDAY.toString()))
            .andExpect(jsonPath("$.checkin").value(DEFAULT_CHECKIN.toString()))
            .andExpect(jsonPath("$.goLunch").value(DEFAULT_GO_LUNCH.toString()))
            .andExpect(jsonPath("$.backLunch").value(DEFAULT_BACK_LUNCH.toString()))
            .andExpect(jsonPath("$.checkout").value(DEFAULT_CHECKOUT.toString())) }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingTimesheet() {
        // Get the timesheet
        restTimesheetMockMvc.perform(get("/api/timesheets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateTimesheet() {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet)

        val databaseSizeBeforeUpdate = timesheetRepository.findAll().size

        // Update the timesheet
        val id = timesheet.id
        assertNotNull(id)
        val updatedTimesheet = timesheetRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTimesheet are not directly saved in db
        em.detach(updatedTimesheet)
        updatedTimesheet.weekday = UPDATED_WEEKDAY
        updatedTimesheet.goLunch = UPDATED_GO_LUNCH
        updatedTimesheet.backLunch = UPDATED_BACK_LUNCH
        updatedTimesheet.checkin = UPDATED_CHECKIN
        updatedTimesheet.checkout = UPDATED_CHECKOUT
        val timesheetDTO = timesheetMapper.toDto(updatedTimesheet)

        restTimesheetMockMvc.perform(
            put("/api/timesheets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(timesheetDTO))
        ).andExpect(status().isOk)

        // Validate the Timesheet in the database
        val timesheetList = timesheetRepository.findAll()
        assertThat(timesheetList).hasSize(databaseSizeBeforeUpdate)
        val testTimesheet = timesheetList[timesheetList.size - 1]
        assertThat(testTimesheet.weekday).isEqualTo(UPDATED_WEEKDAY)
        assertThat(testTimesheet.goLunch).isEqualTo(UPDATED_GO_LUNCH)
        assertThat(testTimesheet.backLunch).isEqualTo(UPDATED_BACK_LUNCH)
        assertThat(testTimesheet.checkin).isEqualTo(UPDATED_CHECKIN)
        assertThat(testTimesheet.checkout).isEqualTo(UPDATED_CHECKOUT)
    }

    @Test
    @Transactional
    fun updateNonExistingTimesheet() {
        val databaseSizeBeforeUpdate = timesheetRepository.findAll().size

        // Create the Timesheet
        val timesheetDTO = timesheetMapper.toDto(timesheet)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimesheetMockMvc.perform(
            put("/api/timesheets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(timesheetDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Timesheet in the database
        val timesheetList = timesheetRepository.findAll()
        assertThat(timesheetList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteTimesheet() {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet)

        val databaseSizeBeforeDelete = timesheetRepository.findAll().size

        // Delete the timesheet
        restTimesheetMockMvc.perform(
            delete("/api/timesheets/{id}", timesheet.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val timesheetList = timesheetRepository.findAll()
        assertThat(timesheetList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_WEEKDAY: LocalDate = LocalDate.now()
        private val UPDATED_WEEKDAY: LocalDate = LocalDate.now(ZoneId.systemDefault())

        private val DEFAULT_GO_LUNCH: LocalTime = LocalTime.parse("12:00")
        private val UPDATED_GO_LUNCH: LocalTime = LocalTime.parse("12:30")

        private val DEFAULT_BACK_LUNCH: LocalTime = LocalTime.parse("13:00")
        private val UPDATED_BACK_LUNCH: LocalTime = LocalTime.parse("14:00")

        private val DEFAULT_CHECKIN: LocalTime = LocalTime.parse("09:00")
        private val UPDATED_CHECKIN: LocalTime = LocalTime.parse("08:00")

        private val DEFAULT_CHECKOUT: LocalTime = LocalTime.parse("18:00")
        private val UPDATED_CHECKOUT: LocalTime = LocalTime.parse("17:00")

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Timesheet {
            val timesheet = Timesheet(
                weekday = DEFAULT_WEEKDAY,
                goLunch = DEFAULT_GO_LUNCH,
                backLunch = DEFAULT_BACK_LUNCH,
                checkin = DEFAULT_CHECKIN,
                checkout = DEFAULT_CHECKOUT
            )

            return timesheet
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Timesheet {
            val timesheet = Timesheet(
                weekday = UPDATED_WEEKDAY,
                goLunch = UPDATED_GO_LUNCH,
                backLunch = UPDATED_BACK_LUNCH,
                checkin = UPDATED_CHECKIN,
                checkout = UPDATED_CHECKOUT
            )

            return timesheet
        }
    }
}
