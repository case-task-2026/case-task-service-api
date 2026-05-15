package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Schema(description = "Standard API error response")
data class ApiErrorResponse(

    @field:Schema(description = "HTTP status code", example = "400")
    val status: Int,

    @field:Schema(description = "HTTP error reason", example = "Bad Request")
    val error: String,

    @field:Schema(description = "Human-readable error message", example = "Request validation failed")
    val message: String,

    @field:Schema(description = "Request path that caused the error", example = "/tasks")
    val path: String,

    @field:Schema(description = "Timestamp when the error occurred", example = "2026-05-14T09:30:00Z")
    val timestamp: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),

    @field:Schema(description = "Field-level validation errors, when applicable")
    val fieldErrors: List<ApiFieldErrorResponse>? = null
)

@Schema(description = "Field-level validation error")
data class ApiFieldErrorResponse(

    @field:Schema(description = "Name of the invalid field", example = "title")
    val field: String,

    @field:Schema(description = "Validation message for the field", example = "Title must not be blank")
    val message: String
)