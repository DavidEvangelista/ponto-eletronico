package br.com.ais.pontoeletronico.service.impl

import br.com.ais.pontoeletronico.domain.Timesheet
import br.com.ais.pontoeletronico.repository.TimesheetRepository
import br.com.ais.pontoeletronico.security.getUserId
import br.com.ais.pontoeletronico.service.TimesheetService
import br.com.ais.pontoeletronico.service.dto.TimesheetDTO
import br.com.ais.pontoeletronico.service.erros.HourBeforeBusinessException
import br.com.ais.pontoeletronico.service.erros.WeekendBusinessException
import br.com.ais.pontoeletronico.service.mapper.TimesheetMapper
import java.time.DayOfWeek
import java.time.Duration
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Implementation for managing [Timesheet].
 */
@Service
@Transactional
class TimesheetServiceImpl(
    private val timesheetRepository: TimesheetRepository,
    private val timesheetMapper: TimesheetMapper
) : TimesheetService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(timesheetDTO: TimesheetDTO): TimesheetDTO {
        log.debug("Request to save Timesheet : $timesheetDTO")

        isWeekend(timesheetDTO)
        validateHours(timesheetDTO)
        val totalMilis: Long = calcHoursTotal(timesheetDTO)
        var timesheet = timesheetMapper.toEntity(timesheetDTO)
        timesheet.total = totalMilis
        getUserId()?.ifPresent { timesheet.userId = it }
        timesheet = timesheetRepository.save(timesheet)
        return timesheetMapper.toDto(timesheet)
    }

    @Transactional(readOnly = true)
    override fun findAll(): MutableList<TimesheetDTO> {
        log.debug("Request to get all Timesheets")
        return timesheetRepository.findAll()
            .mapTo(mutableListOf(), timesheetMapper::toDto)
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<TimesheetDTO> {
        log.debug("Request to get Timesheet : $id")
        return timesheetRepository.findById(id)
            .map(timesheetMapper::toDto)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Timesheet : $id")

        timesheetRepository.deleteById(id)
    }

    /**
     * valida a RGN de não registrar ponto aos finais de semana
     */
    private fun isWeekend(timesheetDTO: TimesheetDTO) {
        val d: DayOfWeek = timesheetDTO.weekday.dayOfWeek
        if (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) {
            throw WeekendBusinessException("Não pode haver registro de ponto no final de semana!")
        }
    }

    private fun validateHours(timesheetDTO: TimesheetDTO) {
        if (!isHoursValid(timesheetDTO)) {
            throw HourBeforeBusinessException("As horas lançadas são inválidas, por favor rever marcações!")
        }
        if(!isLounchValid(timesheetDTO)) {
            throw HourBeforeBusinessException("Horário de almoço deve ser de no mínimo 1 hora")
        }
    }

    /**
     * valida a RGN para não registrar horarios de forma errada
     */
    private fun isHoursValid(timesheetDTO: TimesheetDTO): Boolean {
        if (timesheetDTO.checkin.isBefore(timesheetDTO.goLunch) &&
            timesheetDTO.goLunch.isBefore(timesheetDTO.backLunch) &&
            timesheetDTO.backLunch.isBefore(timesheetDTO.checkout)) {
            return true
        }
        return false
    }

    private fun isLounchValid(timesheetDTO: TimesheetDTO): Boolean {
        return Duration.between(timesheetDTO.goLunch, timesheetDTO.backLunch).toMillis() >= 3600000L
    }

    private fun calcHoursTotal(timesheetDTO: TimesheetDTO): Long {
        return Duration.between(timesheetDTO.checkin, timesheetDTO.checkout).toMillis() - Duration.between(timesheetDTO.goLunch, timesheetDTO.backLunch).toMillis()
    }
}
