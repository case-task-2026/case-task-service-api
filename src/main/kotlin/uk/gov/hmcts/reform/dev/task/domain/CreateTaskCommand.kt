package uk.gov.hmcts.reform.dev.task.domain

import java.time.OffsetDateTime

data class CreateTaskCommand(
    val title: String,
    val description: String?,
    val dueDateTime: OffsetDateTime
)
