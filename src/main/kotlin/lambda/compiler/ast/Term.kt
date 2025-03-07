package lambda.compiler.ast

/**
 * Generic term of a Lambda Calculus program.
 */
sealed interface Term {
    fun <T> accept(visitor: TermVisitor<T>): T
}

/**
 * A named variable.
 *
 * @param name  The name of the variable
 */
data class Variable(val name: String) : Term {
    override fun <T> accept(visitor: TermVisitor<T>) = visitor.visitVariable(this)
}

/**
 *  An abstraction is a function definition, taking some bound symbol as input and returning the function body.
 *
 *  @param param    The bound variable
 *  @param body     The returned body
 */
data class Abstraction(val param: Variable, val body: Term) : Term {
    override fun <T> accept(visitor: TermVisitor<T>) = visitor.visitAbstraction(this)
}

/**
 * Returns true if the abstraction binds the variable.
 * A symbol is bound by an abstraction if it has the same name as its parameter.
 */
fun Abstraction.binds(variable: Variable) = param == variable

/**
 * An application is a function call.
 *
 * @param target    The target of the call (calee)
 * @param arg       The argument of the call
 */
data class Application(val target: Term, val arg: Term) : Term {
    override fun <T> accept(visitor: TermVisitor<T>) = visitor.visitApplication(this)
}
