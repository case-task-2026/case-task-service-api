package uk.gov.hmcts.reform.dev.task.domain

import java.time.OffsetDateTime

class Task private constructor(
    val id: TaskId,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val dueDatetime: OffsetDateTime,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
){
    fun updateStatus(newStatus: TaskStatus, updatedAt: OffsetDateTime): Task {
       validateUpdatedAt(createdAt = createdAt, updatedAt = updatedAt)

       if (status == newStatus) {
           return this
       }
        return Task(
            id = id,
            title = title,
            description = description,
            status = newStatus,
            dueDatetime = dueDatetime,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun updateDetails(
        title: String,
        description: String?,
        dueDatetime: OffsetDateTime,
        updatedAt: OffsetDateTime
    ): Task {
        validateUpdatedAt(createdAt = createdAt, updatedAt = updatedAt)

        return Task(
            id = id,
            title = TaskTextNormalizer.normalizeTitle(title),
            description = TaskTextNormalizer.normalizeDescription(description),
            status = status,
            dueDatetime = dueDatetime,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun createNew(
            id: TaskId,
            title: String,
            description: String?,
            dueDatetime: OffsetDateTime,
            createdAt: OffsetDateTime
        ): Task {
            return Task(
                id = id,
                title = TaskTextNormalizer.normalizeTitle(title),
                description = TaskTextNormalizer.normalizeDescription(description),
                status = TaskStatus.TODO,
                dueDatetime = dueDatetime,
                createdAt = createdAt,
                updatedAt = createdAt
            )
        }

        fun restore(
            id: TaskId,
            title: String,
            description: String?,
            status: TaskStatus,
            dueDateTime: OffsetDateTime,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime
        ): Task{
            validateUpdatedAt(createdAt = createdAt, updatedAt = updatedAt)

            return Task(
                id = id,
                title = TaskTextNormalizer.normalizeTitle(title),
                description = TaskTextNormalizer.normalizeDescription(description),
                status = status,
                dueDatetime = dueDateTime,
                createdAt = createdAt,
                updatedAt = createdAt
            )
        }


        private fun validateUpdatedAt(createdAt: OffsetDateTime, updatedAt: OffsetDateTime) {
            if (updatedAt.isBefore(createdAt)){
                throw TaskValidationException("Task updatedAt must not be before createdAt")
            }
        }
    }
}