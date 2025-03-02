import lambda.common.algorithm.serialize
import lambda.common.invoke
import lambda.common.lambda
import lambda.compiler.ast.Symbol


fun main() {
    val x = Symbol("x")
    val y = Symbol("y")
    val z = Symbol("z")
    val program = lambda(z) { (lambda(y) { y(lambda(x) { x }) })(lambda(x) { z(x) }) }

    println(program.serialize())
}
