package lambda.common.algorithm

import lambda.compiler.ast.Application
import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Symbol
import lambda.compiler.ast.Term
import lambda.compiler.ast.TermVisitor

/**
 * Serializes a Lambda Calculus term to a string.
 */
data object Serializer : TermVisitor<String> {
    override fun visitSymbol(sym: Symbol) = sym.name

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
                is Symbol -> it
                else -> "($it)"
            }
        }
        return "$serializedTarget $serializedArg"
    }
}

fun Term.serialize() = accept(Serializer)
