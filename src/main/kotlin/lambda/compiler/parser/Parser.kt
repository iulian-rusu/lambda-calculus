package lambda.compiler.parser

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Term
import lambda.compiler.ast.Variable
import lambda.compiler.lexer.Lexer
import lambda.compiler.lexer.Token
import lambda.compiler.lexer.TokenKind
import lambda.compiler.diagnostic.Expectation
import lambda.compiler.diagnostic.Result
import lambda.compiler.diagnostic.SyntaxError
import lambda.compiler.diagnostic.TokenKindExpectation
import lambda.compiler.diagnostic.asResult
import lambda.compiler.diagnostic.andThen
import lambda.compiler.diagnostic.map
import lambda.compiler.diagnostic.valueOrElse

class Parser(source: String) {
    private val lexer: Lexer = Lexer(source)

    /**
     * Parses the source code into a Lambda Calculus term.
     *
     * @return The parsed term, or a source analysis error
     */
    fun parse(): Result<Term> = parseUntil { false }

    /**
     * Parses until one of the following happens:
     * - There are no more tokens (i.e. the end of source is reached).
     * - A source analysis error happens (e.g. a lexical or syntax error).
     * - The next token to be parsed satisfies the stop condition.
     *
     * @param shouldStop    Stop condition based on the token about to be consumed
     */
    private fun parseUntil(shouldStop: (Token) -> Boolean): Result<Term> {
        fun parseFromToken(startToken: Token): Result<Term> = when (startToken.kind) {
            TokenKind.DOT, TokenKind.RIGHT_PAREN -> unexpectedToken(START_TOKEN_EXPECTATION, startToken)
            TokenKind.IDENT -> Result.Ok(Variable(startToken.value))
            TokenKind.REVERSE_SOLIDUS -> {
                expect(TokenKind.IDENT)
                    .andThen { ident -> expect(TokenKind.DOT).map { ident } }
                    .andThen { ident ->
                        parseUntil(shouldStop).map { term ->
                            Abstraction(
                                param = Variable(ident.value),
                                body = term
                            )
                        }
                    }
            }

            TokenKind.LEFT_PAREN -> {
                val term = parseUntil { it.kind == TokenKind.RIGHT_PAREN }
                // Ensure parentheses are balanced
                expect(TokenKind.RIGHT_PAREN).andThen { term }
            }
        }

        var accumulator: Term? = null

        while (true) {
            val token = lexer.peek()
                ?.valueOrElse { err -> return err }
                ?.takeUnless(shouldStop)
                ?: break
            // It is safe to call next() since the peeked token is present
            lexer.next()

            val parsedTerm = parseFromToken(token)
                .valueOrElse { err -> return err }

            accumulator = combine(accumulator, parsedTerm)
        }

        return accumulator
            ?.let { Result.Ok(it) }
            ?: unexpectedEndOfTerm(START_TOKEN_EXPECTATION)
    }

    private fun expect(expected: TokenKind): Result<Token> = lexer.nextOrNull()
        ?.andThen { it.expectKind(expected) }
        ?: unexpectedEndOfTerm(TokenKindExpectation.of(expected))

    private fun Token.expectKind(expected: TokenKind) = when (kind) {
        expected -> Result.Ok(this)
        else -> unexpectedToken(TokenKindExpectation.of(expected), this)
    }

    private fun combine(currentTerm: Term?, newTerm: Term) = when (currentTerm) {
        null -> newTerm
        else -> Application(target = currentTerm, arg = newTerm)
    }

    private fun unexpectedEndOfTerm(expectation: Expectation) =
        SyntaxError.UnexpectedEndOfTerm(expectation, lexer.location()).asResult()

    private fun unexpectedToken(expectation: Expectation, actualToken: Token) =
        SyntaxError.UnexpectedToken(expectation, actualToken).asResult()

    companion object {
        val START_TOKEN_EXPECTATION = TokenKindExpectation.of(
            TokenKind.IDENT,
            TokenKind.REVERSE_SOLIDUS,
            TokenKind.LEFT_PAREN
        )
    }
}
