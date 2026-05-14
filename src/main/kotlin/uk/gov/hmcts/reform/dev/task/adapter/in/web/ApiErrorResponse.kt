package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import java.time.OffsetDateTime
import java.time.ZoneOffset

data class ApiErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),
    val fieldErrors: List<ApiFieldErrorResponse>? = null
)

data class ApiFieldErrorResponse(
    val field: String,
    val message: String
)
