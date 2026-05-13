package uk.gov.hmcts.reform.dev.task.application

import uk.gov.hmcts.reform.dev.task.domain.TaskId
import java.time.OffsetDateTime

class UpdateTaskDetailsCommand(
    val taskId: TaskId,
    val title: String,
    val description: String?,
    val dueDateTime: OffsetDateTime
)