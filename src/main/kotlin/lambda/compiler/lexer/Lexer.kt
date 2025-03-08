package lambda.compiler.lexer

import lambda.compiler.common.Location
import lambda.compiler.common.SourceCursor
import lambda.compiler.common.Span
import lambda.compiler.result.LexicalError
import lambda.compiler.result.Result

typealias TokenResult = Result<Token>

data object Lexer {
    fun tokenize(source: String) = tokenize(SourceCursor(source))

    private fun tokenize(cursor: SourceCursor): Sequence<TokenResult> = sequence {
        suspend fun SequenceScope<TokenResult>.yieldToken(
            kind: TokenKind,
            span: Span,
            location: Location
        ) = yield(
            Result.Ok(
                Token(kind, value = cursor[span], span, location)
            )
        )

        suspend fun SequenceScope<TokenResult>.yieldError(error: LexicalError) = yield(Result.Error(error))

        suspend fun SequenceScope<TokenResult>.tokenizeCurrentChar(kind: TokenKind) {
            yieldToken(kind, cursor.currentCharSpan, cursor.location)
            cursor.advance()
        }

        suspend fun SequenceScope<TokenResult>.tokenizeIdentifier() {
            val beginIndex = cursor.index
            val beginLocation = cursor.location
            advanceToIdentifierEnd(cursor)
            yieldToken(
                TokenKind.IDENT,
                Span(begin = beginIndex, end = cursor.index),
                beginLocation
            )
        }

        suspend fun SequenceScope<TokenResult>.handleInvalidChar(char: Char) {
            yieldError(
                LexicalError.InvalidChar(
                    char = char,
                    location = cursor.location,
                )
            )
            cursor.advance()
        }

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

    private fun advanceToIdentifierEnd(cursor: SourceCursor) {
        do {
            cursor.advance()
        } while (cursor.currentChar?.isIdentifierContent() == true)
    }

    private fun Char.isIgnored() = isWhitespace()
    private fun Char.isIdentifierStart() = isLetter() || this == '_'
    private fun Char.isIdentifierContent() = isDigit() || isIdentifierStart()
}
