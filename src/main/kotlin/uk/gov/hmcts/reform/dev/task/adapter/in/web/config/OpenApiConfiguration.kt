package uk.gov.hmcts.reform.dev.task.adapter.`in`.web.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

    @Bean
    fun taskManagementOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Case Task Service API")
                    .description(
                        """
                            Backend API for managing caseworker tasks.
                            
                            This service allows caseworkers to create, view, update, and delete tasks.
                            It is implemented as a Kotlin Spring Boot REST API with PostgreSQL persistence,
                            validation, error handling, and OpenAPI documentation.
                        """.trimIndent()
                    )
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Candidate")
                    )
                    .license(
                        License()
                            .name("For assessment use")
                    )
            )
    }
}