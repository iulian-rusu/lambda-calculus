package lambda.compiler.result

sealed interface Result<out T> {
    val valueOrNull: T?
        get() = when (this) {
            is Ok -> value
            else -> null
        }

    val errorOrNull: SourceAnalysisError?
        get() = when (this) {
            is Error -> error
            else -> null
        }

    data class Ok<T>(val value: T) : Result<T>
    data class Error(val error: SourceAnalysisError) : Result<Nothing>
}

fun <T, U> Result<T>.map(mapper: (T) -> U) = when (this) {
    is Result.Ok -> Result.Ok(mapper(value))
    is Result.Error -> this
}
