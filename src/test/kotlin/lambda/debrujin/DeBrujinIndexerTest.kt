package lambda.debrujin

import lambda.common.invoke
import lambda.common.lambda
import lambda.compiler.ast.Term
import lambda.compiler.ast.Variable
import lambda.debrujin.ast.DeBrujinTerm
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class DeBrujinIndexerTest {
    @ParameterizedTest
    @MethodSource("getTermsAndExpectedIndexings")
    fun `indexing a term returns the expected indexing`(term: Term, expected: DeBrujinTerm) {
        assertEquals(expected, term.deBrujin())
    }

    companion object {
        @JvmStatic
        fun getTermsAndExpectedIndexings(): List<Arguments> {
            val x = Variable("x")
            val y = Variable("y")
            val z = Variable("z")

            return listOf(
                Arguments.of(x, x.constant),
                Arguments.of(x(y), x.constant(y.constant)),
                Arguments.of(lambda(x) { x }, lambda { 1.index }),
                Arguments.of(lambda(x) { y }, lambda { y.constant }),
                Arguments.of(lambda(x) { lambda(y) { x(y) } }, lambda { lambda { 2.index(1.index) } }),
                Arguments.of(
                    lambda(x) { lambda(y) { lambda(z) { y(x) } } },
                    lambda { lambda { lambda { 2.index(3.index) } } },
                ),
                Arguments.of(
                    lambda(x) { lambda(y) { lambda(x) { y(x) } } },
                    lambda { lambda { lambda { 2.index(1.index) } } },
                ),
                Arguments.of(
                    lambda(x) { x(lambda(x) { x(lambda(x) { y(x) }) }) },
                    lambda { 1.index(lambda { 1.index(lambda { y.constant(1.index) }) }) },
                ),
                Arguments.of(
                    lambda(z) { (lambda(y) { y(lambda(x) { x }) })(lambda(x) { z(x) }) },
                    lambda { (lambda { 1.index(lambda { 1.index }) })(lambda { 2.index(1.index) }) }
                ),
            )
        }
    }
}
