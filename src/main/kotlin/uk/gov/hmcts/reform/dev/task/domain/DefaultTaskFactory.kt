package uk.gov.hmcts.reform.dev.task.domain

import java.time.Clock
import java.time.OffsetDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class DefaultTaskFactory (
    private val taskIdGenerator: TaskIdGenerator = UuidTaskGenerator(),
    private val clock: Clock = Clock.systemUTC()
): TaskFactory {

    override fun create(command: CreateTaskCommand): Task {
        val createdAt = OffsetDateTime.now(clock)

        return Task.createNew(
            id = taskIdGenerator.generate(),
            title = command.title,
            description = command.description,
            dueDatetime = command.dueDateTime,
            createdAt = createdAt
        )
    }
}