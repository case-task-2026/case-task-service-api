package uk.gov.hmcts.reform.dev.task.domain

internal object TaskTextNormalizer {

    fun normalizeTitle(title: String): String {
        val normalizedTitle = title.trim()

        if (normalizedTitle.isBlank() ) {
            throw TaskValidationException("Task title must not be blank")
        }

        if (normalizedTitle.length > TaskRules.MAX_TITLE_LENGTH) {
            throw TaskValidationException(
                "Task title must not exceed ${TaskRules.MAX_TITLE_LENGTH} characters"
            )
        }
        return normalizedTitle
    }

    fun normalizeDescription(description: String?): String? {
        val normalizeDescription = description?.trim()

        if (normalizeDescription.isNullOrBlank()) {
            return null
        }

        if (normalizeDescription.length > TaskRules.MAX_DESCRIPTION_LENGTH) {
            throw TaskValidationException(
                "Task description must not exceed ${TaskRules.MAX_DESCRIPTION_LENGTH} characters"
            )
        }
        return normalizeDescription
    }
}