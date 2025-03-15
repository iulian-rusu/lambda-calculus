package lambda.compiler.diagnostic

/**
 * Models the outcome of a source analysis operation that can result in an error.
 */
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

/**
 * Functorial transformation for the value held by the result.
 * Has no effect if the result holds an error.
 */
inline fun <T, U> Result<T>.map(mapper: (T) -> U): Result<U> = when (this) {
    is Result.Ok -> Result.Ok(mapper(value))
    is Result.Error -> this
}

/**
 * Monadic composition of the current result with a result-producing method.
 * Has no effect if the result holds an error.
 */
inline fun <T, U> Result<T>.andThen(continuation: (T) -> Result<U>): Result<U> = when (this) {
    is Result.Ok -> continuation(value)
    is Result.Error -> this
}

/**
 * Extracts the value held by the result, or calls a default value supplier if the result is an error.
 */
inline fun <T> Result<T>.valueOrElse(orElse: (Result.Error) -> T) = when (this) {
    is Result.Error -> orElse(this)
    is Result.Ok -> value
}

fun SourceAnalysisError.asResult() = Result.Error(this)
