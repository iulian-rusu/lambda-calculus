package lambda.debrujin

import lambda.compiler.ast.Variable
import lambda.debrujin.ast.DeBrujinApplication
import lambda.debrujin.ast.DeBrujinBinder
import lambda.debrujin.ast.DeBrujinConstant
import lambda.debrujin.ast.DeBrujinIndex
import lambda.debrujin.ast.DeBrujinTerm

operator fun DeBrujinTerm.invoke(arg: DeBrujinTerm) = DeBrujinApplication(
    target = this,
    arg = arg
)

fun lambda(expr: () -> DeBrujinTerm) = DeBrujinBinder(
    expr = expr()
)

val Variable.constant
    get() = DeBrujinConstant(value = name)

val Int.index
    get() = DeBrujinIndex(value = this)


