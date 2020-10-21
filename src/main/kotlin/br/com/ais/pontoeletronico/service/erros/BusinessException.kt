package br.com.ais.pontoeletronico.service.erros

import org.zalando.problem.AbstractThrowableProblem
import org.zalando.problem.Exceptional
import org.zalando.problem.Status

abstract class BusinessException(defaultMessage: String) :
    AbstractThrowableProblem(
        DEFAULT_TYPE, defaultMessage, Status.BAD_REQUEST, null, null, null
    ) {
    override fun getCause(): Exceptional? = super.cause
}
