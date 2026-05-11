package uk.gov.hmcts.reform.dev.task.domain

import java.util.UUID

@JvmInline
value class TaskId(val value: UUID) {
    override fun toString(): String {
        return value.toString()
    }

    companion object {
        fun from(value: UUID): TaskId {
            return TaskId(value)
        }

        fun from(value: String): TaskId {
            return TaskId(UUID.fromString(value))
        }
    }
}