package lambda.compiler.common

/**
 * Iterator-like abstraction that tracks the current line and column
 * while traversing a source string.
 *
 * This object mutates its internal state whenever it navigates the source.
 */
class SourceCursor(private val source: String) {
    var index = 0
        private set

    private var line = 1
    private var column = 1

    val location: Location
        get() = Location(line = line, column = column)

    val currentChar: Char?
        get() = source.getOrNull(index)

    val currentCharSpan: Span
        get() = Span.ofSingleChar(index)

    operator fun get(span: Span) = source.substring(span.begin, span.end)

    fun advance() = currentChar?.let { advanceFromChar(it) } ?: this

    private fun advanceFromChar(char: Char) = when (char) {
        '\n' -> advanceLine()
        else -> advanceColumn()
    }

    private fun advanceLine(): SourceCursor {
        ++line
        ++index
        column = 1
        return this
    }

    private fun advanceColumn(): SourceCursor {
        ++column
        ++index
        return this
    }
}
