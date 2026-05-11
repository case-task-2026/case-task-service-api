package uk.gov.hmcts.reform.dev.task.domain

fun interface TaskFactory {
    fun create(command: CreateTaskCommand): Task
}