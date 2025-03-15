package lambda.compiler.diagnostic

import lambda.compiler.common.Location
import lambda.compiler.lexer.Token

sealed interface SourceAnalysisError {
    val location: Location
}

sealed interface LexicalError : SourceAnalysisError {
    data class InvalidChar(val char: Char, override val location: Location) : LexicalError
}

sealed interface SyntaxError : SourceAnalysisError {
    sealed interface ExpectationError : SourceAnalysisError {
        val expectation: Expectation
    }

    data class UnexpectedEndOfTerm(
        override val expectation: Expectation,
        override val location: Location
    ) : ExpectationError

    data class UnexpectedToken(
        override val expectation: Expectation,
        val actualToken: Token
    ) : ExpectationError {
        override val location: Location
            get() = actualToken.location
    }
}
