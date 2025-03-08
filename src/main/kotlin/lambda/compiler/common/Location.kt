package lambda.compiler.common

/**
 * Models the location inside a source string.
 *
 * @param line      The line number, starting from 1
 * @param column    The column number, starting from 1
 */
data class Location(val line: Int, val column: Int) {
   fun display() = "$line:$column"
}
