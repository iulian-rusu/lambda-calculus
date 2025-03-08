package lambda.compiler.lexer

import lambda.compiler.common.Location
import lambda.compiler.common.Span

enum class TokenKind {
    REVERSE_SOLIDUS,
    DOT,
    LEFT_PAREN,
    RIGHT_PAREN,
    IDENT
}

data class Token(
    val kind: TokenKind,
    val value: String,
    val span: Span,
    val location: Location
) {
    fun display() = "${location.display()} [${span.display()}]=$kind('$value')"
}
