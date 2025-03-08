package lambda.compiler.result

import lambda.compiler.common.Location

sealed interface SourceAnalysisError {
    val location: Location
}

sealed interface LexicalError : SourceAnalysisError {
    data class InvalidChar(val char: Char, override val location: Location) : LexicalError
}
