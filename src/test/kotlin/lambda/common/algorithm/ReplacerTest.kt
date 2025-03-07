package lambda.common.algorithm

import lambda.common.invoke
import lambda.common.lambda
import lambda.compiler.ast.Application
import lambda.compiler.ast.Variable
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ReplacerTest {
    private val x = Variable("x")
    private val y = Variable("y")
    private val z = Variable("z")

    @Test
    fun `replacing a non-existing variable does nothing`() {
        val term = x(y)
        assertEquals(term, term.replace(z, x))
    }

    @Test
    fun `replacing a variable returns the expected program`() {
        assertEquals(z(y), x(y).replace(x, z))
    }

    @Test
    fun `replacing a variable replaces all non-shadowed occurences`() {
        val term = x(
            lambda(y) {
                lambda(z) {
                    x(y)(lambda(x) { x(z) })
                }
            }
        )

        val expected = z(
            lambda(y) {
                lambda(z) {
                    z(y)(lambda(x) { x(z) })
                }
            }
        )
        assertEquals(expected, term.replace(x, z))
    }

    @Test
    fun `replacing a variable with a complex term returns expected results`() {
        val term = x(y)(lambda(z) { x(x)(z)(lambda(x) { x }(x)) })
        val replacement = (lambda(x) { lambda(y) { y(x)(y) } })(lambda(z) { x(z) })
        val expected = replacement(y)(lambda(z) { replacement(replacement)(z)(lambda(x) { x }(replacement)) })
        assertEquals(expected, term.replace(x, replacement))
    }
}
