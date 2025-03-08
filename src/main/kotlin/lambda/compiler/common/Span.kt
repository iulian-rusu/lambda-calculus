package lambda.compiler.common

/**
 * Models the range of indices occupied by a token.
 *
 * @param begin The index where the span begins (inclusive)
 * @param end   The index where the span ends (exclusive), cannot be less than `begin`
 */
data class Span(val begin: Int, val end: Int) {
    init {
        check(begin <= end) {
            "Invalid bounds for span ${display()}"
        }
    }

    val length: Int
        get() = end - begin

    fun display() = "$begin..$end"

    companion object {
        fun ofSingleChar(index: Int) = Span(begin = index, end = index + 1)
    }
}
