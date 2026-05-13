package uk.gov.hmcts.reform.dev.task.application.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.hmcts.reform.dev.task.domain.DefaultTaskFactory
import uk.gov.hmcts.reform.dev.task.domain.TaskFactory
import uk.gov.hmcts.reform.dev.task.domain.TaskIdGenerator
import uk.gov.hmcts.reform.dev.task.domain.UuidTaskGenerator
import java.time.Clock

@Configuration
class TaskApplicationConfiguration {

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }

    @Bean
    fun taskIdGenerator(): TaskIdGenerator {
        return UuidTaskGenerator()
    }

    @Bean
    fun taskFactory(
        taskIdGenerator: TaskIdGenerator,
        clock: Clock
    ): TaskFactory {
        return DefaultTaskFactory(
            taskIdGenerator = taskIdGenerator,
            clock = clock
        )
    }
}