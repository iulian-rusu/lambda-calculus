package lambda.common

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Term
import lambda.compiler.ast.Variable

operator fun Term.invoke(arg: Term) = Application(
    target = this,
    arg = arg
)

fun lambda(param: Variable, body: () -> Term) = Abstraction(
    param = param,
    body = body()
)

fun lambda(param: String, body: () -> Term) = lambda(Variable(param), body)
