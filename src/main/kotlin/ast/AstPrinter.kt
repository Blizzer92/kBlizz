package at.kblizz.ast

import at.kblizz.token.Token
import at.kblizz.token.TokenType


internal class AstPrinter : Expr.Visitor<String?> {

    fun print(expr: Expr): String? {
        return expr.accept<String?>(this)
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(
            expr.operator.lexeme,
            expr.left, expr.right
        )
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String? {
        if (expr.value == null) return "nil"
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String?, vararg exprs: Expr): String {
        val builder = StringBuilder()

        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept<String?>(this))
        }
        builder.append(")")

        return builder.toString()
    }

}