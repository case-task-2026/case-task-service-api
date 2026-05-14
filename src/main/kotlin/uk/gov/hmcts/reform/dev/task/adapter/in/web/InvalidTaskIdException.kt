package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

class InvalidTaskIdException(taskId: String): RuntimeException(
    "Task id must be a valid UUID: '$taskId'"
)