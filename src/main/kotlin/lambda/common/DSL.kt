package lambda.common

import lambda.compiler.ast.Application
import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Symbol
import lambda.compiler.ast.Term

operator fun Term.invoke(arg: Term) = Application(
    target = this,
    arg = arg
)

fun lambda(param: Symbol, body: () -> Term) = Abstraction(
    param = param,
    body = body()
)
