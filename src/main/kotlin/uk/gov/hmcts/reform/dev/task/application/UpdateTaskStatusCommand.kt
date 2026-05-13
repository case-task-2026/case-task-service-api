package uk.gov.hmcts.reform.dev.task.application

import uk.gov.hmcts.reform.dev.task.domain.TaskId
import uk.gov.hmcts.reform.dev.task.domain.TaskStatus

data class UpdateTaskStatusCommand(
    val taskId: TaskId,
    val status: TaskStatus
)
