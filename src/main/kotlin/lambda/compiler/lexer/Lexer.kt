package lambda.compiler.lexer

import lambda.compiler.common.Location
import lambda.compiler.common.SourceCursor
import lambda.compiler.common.Span
import lambda.compiler.diagnostic.LexicalError
import lambda.compiler.diagnostic.Result
import lambda.compiler.diagnostic.asResult
import lambda.compiler.diagnostic.map
import lambda.compiler.diagnostic.valueOrElse

typealias TokenResult = Result<Token>

class Lexer(source: String) : Iterator<TokenResult> {
    private val cursor: SourceCursor = SourceCursor(source)
    private val tokens: List<TokenResult> = tokenSequence().toList()

    /**
     * The index of the next token to be consumed by the iterator.
     */
    private var index: Int = 0

    /**
     * Queries whether the iterator has more elements.
     */
    override fun hasNext() = index < tokens.size

    /**
     * Consumes and returns the next token without checking if it exists.
     *
     * @throws NoSuchElementException if the iterator has no next element
     */
    override fun next() = nextOrNull() ?: throw NoSuchElementException("There are no more tokens")

    /**
     * Consumes and returns the next token if it exists, otherwise returns null.
     */
    fun nextOrNull(): TokenResult? = tokens.getOrNull(index++)

    /**
     * Looks ahead at the next token without consuming it.
     */
    fun peek(): TokenResult? = tokens.getOrNull(index)

    /**
     * The location where processing of the next token starts.
     */
    fun location(): Location = when {
        index > tokens.lastIndex -> cursor.location
        else -> tokens[index]
            .map { it.location }
            .valueOrElse { it.error.location }
    }

    private fun tokenSequence(): Sequence<TokenResult> = sequence {
        while (true) {
            val char = cursor.currentChar ?: break
            when {
                char.isIgnored() -> cursor.advance()
                char == '\\' -> tokenizeCurrentChar(TokenKind.REVERSE_SOLIDUS)
                char == '.' -> tokenizeCurrentChar(TokenKind.DOT)
                char == '(' -> tokenizeCurrentChar(TokenKind.LEFT_PAREN)
                char == ')' -> tokenizeCurrentChar(TokenKind.RIGHT_PAREN)
                char.isIdentifierStart() -> tokenizeIdentifier()
                else -> handleInvalidChar(char)
            }
        }
    }

    private suspend fun SequenceScope<TokenResult>.tokenizeCurrentChar(kind: TokenKind) {
        yieldToken(kind, cursor.currentCharSpan, cursor.location)
        cursor.advance()
    }

    private suspend fun SequenceScope<TokenResult>.tokenizeIdentifier() {
        val beginIndex = cursor.index
        val beginLocation = cursor.location
        advanceToIdentifierEnd(cursor)
        yieldToken(
            TokenKind.IDENT,
            Span(begin = beginIndex, end = cursor.index),
            beginLocation
        )
    }

    private fun advanceToIdentifierEnd(cursor: SourceCursor) {
        do {
            cursor.advance()
        } while (cursor.currentChar?.isIdentifierContent() == true)
    }

    private suspend fun SequenceScope<TokenResult>.handleInvalidChar(char: Char) {
        yield(
            LexicalError.InvalidChar(
                char = char,
                location = cursor.location,
            ).asResult()
        )
        cursor.advance()
    }

    private suspend fun SequenceScope<TokenResult>.yieldToken(kind: TokenKind, span: Span, location: Location) =
        yield(Result.Ok(Token(kind, value = cursor[span], span, location)))

    private fun Char.isIgnored() = isWhitespace()
    private fun Char.isIdentifierStart() = isLetter() || this == '_'
    private fun Char.isIdentifierContent() = isDigit() || isIdentifierStart()
}
