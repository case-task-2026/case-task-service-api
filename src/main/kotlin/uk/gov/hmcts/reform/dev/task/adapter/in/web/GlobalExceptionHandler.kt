package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import uk.gov.hmcts.reform.dev.task.application.TaskNotFoundException
import uk.gov.hmcts.reform.dev.task.domain.TaskValidationException

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val fieldErrors = exception.bindingResult.fieldErrors
            .map {
                ApiFieldErrorResponse(
                    field = it.field,
                    message = it.defaultMessage ?: "Invalid value"
                )
            }
            .distinct()

        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Request validation failed",
            path = extractPath(request),
            fieldErrors = fieldErrors
        )
    }

    override fun handleHttpMessageNotReadable(
        exception: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Request body is malformed or contains invalid values",
            path = extractPath(request)
        )
    }

    override fun handleHttpRequestMethodNotSupported(
        exception: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val method = exception.method ?: "HTTP method"

        return buildErrorResponse(
            status = HttpStatus.METHOD_NOT_ALLOWED,
            message = "$method is not supported for this endpoint",
            path = extractPath(request)
        )
    }

    @ExceptionHandler(InvalidTaskIdException::class)
    fun handleInvalidTaskIdException(
        exception: InvalidTaskIdException,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = exception.message ?: "Task id must be a valid UUID",
            path = request.requestURI
        )
    }

    @ExceptionHandler(TaskValidationException::class)
    fun handleTaskValidationException(
        exception: TaskValidationException,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = exception.message ?: "Task validation failed",
            path = request.requestURI
        )
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFoundException(
        exception: TaskNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return buildErrorResponse(
            status = HttpStatus.NOT_FOUND,
            message = exception.message ?: "Task was not found",
            path = request.requestURI
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        exception: Exception,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return buildErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "An unexpected error occurred",
            path = request.requestURI
        )
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String,
        path: String,
        fieldErrors: List<ApiFieldErrorResponse>? = null
    ): ResponseEntity<Any> {
        val response = ApiErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = path,
            fieldErrors = fieldErrors
        )

        return ResponseEntity
            .status(status)
            .body(response)
    }

    private fun extractPath(request: WebRequest): String {
        return (request as? ServletWebRequest)
            ?.request
            ?.requestURI
            ?: "unknown"
    }
}