package lambda.debrujin.algorithm

import lambda.debrujin.ast.DeBrujinApplication
import lambda.debrujin.ast.DeBrujinBinder
import lambda.debrujin.ast.DeBrujinConstant
import lambda.debrujin.ast.DeBrujinIndex
import lambda.debrujin.ast.DeBrujinTerm
import lambda.debrujin.ast.DeBrujinVisitor

data object DeBrujinSerializer : DeBrujinVisitor<String> {
    override fun visitIndex(index: DeBrujinIndex) = index.value.toString()

    override fun visitConstant(constant: DeBrujinConstant) = constant.value.let {
        if (it.isNumeric()) "'$it'" else it
    }

    override fun visitBinder(binder: DeBrujinBinder) = "λ ${binder.expr.accept(this)}"

    override fun visitApplication(app: DeBrujinApplication): String {
        val serializedTarget = app.target.accept(this).let {
            when (app.target) {
                is DeBrujinBinder -> "($it)"
                else -> it
            }
        }
        val serializedArg = app.arg.accept(this).let {
            when (app.arg) {
                is DeBrujinIndex, is DeBrujinConstant -> it
                else -> "($it)"
            }
        }
        return "$serializedTarget $serializedArg"
    }

    private fun String.isNumeric() = all { it.isDigit() }
}

fun DeBrujinTerm.serialize() = accept(DeBrujinSerializer)
