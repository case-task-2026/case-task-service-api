package uk.gov.hmcts.reform.dev.task.domain

fun interface TaskIdGenerator {
    fun generate(): TaskId
}