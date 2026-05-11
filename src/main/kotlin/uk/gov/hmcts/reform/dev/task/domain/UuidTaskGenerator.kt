package uk.gov.hmcts.reform.dev.task.domain

import java.util.UUID

class UuidTaskGenerator: TaskIdGenerator {
    override fun generate(): TaskId {
        return TaskId.from(UUID.randomUUID())
    }
}