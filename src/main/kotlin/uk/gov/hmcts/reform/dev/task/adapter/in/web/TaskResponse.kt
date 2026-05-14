package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import uk.gov.hmcts.reform.dev.task.domain.TaskStatus
import java.time.OffsetDateTime

data class TaskResponse(
    val id: String,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val dueDateTime: OffsetDateTime,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)
