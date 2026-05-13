package uk.gov.hmcts.reform.dev.task.application

import uk.gov.hmcts.reform.dev.task.domain.TaskId

class TaskNotFoundException(taskId: TaskId): RuntimeException(
    "Task with id '$taskId' was not found"
)