package lambda.common.algorithm

import lambda.compiler.ast.Application
import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Variable
import lambda.compiler.ast.Term
import lambda.compiler.ast.TermVisitor

/**
 * Serializes a Lambda Calculus term to a string.
 */
data object Serializer : TermVisitor<String> {
    override fun visitVariable(variable: Variable) = variable.name

    override fun visitAbstraction(abs: Abstraction) = "λ${abs.param.name}.${abs.body.accept(this)}"

    override fun visitApplication(app: Application): String {
        val serializedTarget = app.target.accept(this).let {
            when (app.target) {
                is Abstraction -> "($it)"
                else -> it
            }
        }
        val serializedArg = app.arg.accept(this).let {
            when (app.arg) {
                is Variable -> it
                else -> "($it)"
            }
        }
        return "$serializedTarget $serializedArg"
    }
}

fun Term.serialize() = accept(Serializer)
