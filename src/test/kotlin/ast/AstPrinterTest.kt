package ast

import at.kblizz.ast.AstPrinter
import at.kblizz.ast.Expr
import at.kblizz.token.Token
import at.kblizz.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals

class AstPrinterTest {
    private val printer = AstPrinter()

    @Test
    fun printsLiteralNumber() {
        val expr = Expr.Literal(123)
        assertEquals("123", printer.print(expr))
    }

    @Test
    fun printsLiteralString() {
        val expr = Expr.Literal("hello")
        assertEquals("hello", printer.print(expr))
    }

    @Test
    fun printsUnaryExpression() {
        val expr = Expr.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Literal(123)
        )
        assertEquals("(- 123)", printer.print(expr))
    }

    @Test
    fun printsBinaryExpression() {
        val expr = Expr.Binary(
            Expr.Literal(1),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Literal(2)
        )
        assertEquals("(+ 1 2)", printer.print(expr))
    }

    @Test
    fun printsGroupingExpression() {
        val expr = Expr.Grouping(Expr.Literal(45.67))
        assertEquals("(group 45.67)", printer.print(expr))
    }

    @Test
    fun printsComplexNestedExpression() {
        // -123 * (45.67)
        val expr = Expr.Binary(
            Expr.Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Expr.Literal(123)
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Grouping(
                Expr.Literal(45.67)
            )
        )
        assertEquals("(* (- 123) (group 45.67))", printer.print(expr))
    }

    @Test
    fun printsDeeplyNestedExpression() {
        // (1 + 2) * (3 - 4)
        val expr = Expr.Binary(
            Expr.Grouping(
                Expr.Binary(
                    Expr.Literal(1),
                    Token(TokenType.PLUS, "+", null, 1),
                    Expr.Literal(2)
                )
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Grouping(
                Expr.Binary(
                    Expr.Literal(3),
                    Token(TokenType.MINUS, "-", null, 1),
                    Expr.Literal(4)
                )
            )
        )
        assertEquals("(* (group (+ 1 2)) (group (- 3 4)))", printer.print(expr))
    }

    @Test
    fun printsMultipleUnaryOperators() {
        // --123
        val expr = Expr.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Expr.Literal(123)
            )
        )
        assertEquals("(- (- 123))", printer.print(expr))
    }

    @Test
    fun printsBangOperator() {
        val expr = Expr.Unary(
            Token(TokenType.BANG, "!", null, 1),
            Expr.Literal(true)
        )
        assertEquals("(! true)", printer.print(expr))
    }

    @Test
    fun printsComparisonOperators() {
        val expr = Expr.Binary(
            Expr.Literal(5),
            Token(TokenType.GREATER, ">", null, 1),
            Expr.Literal(3)
        )
        assertEquals("(> 5 3)", printer.print(expr))
    }

    @Test
    fun printsEqualityOperators() {
        val expr = Expr.Binary(
            Expr.Literal(1),
            Token(TokenType.EQUAL_EQUAL, "==", null, 1),
            Expr.Literal(1)
        )
        assertEquals("(== 1 1)", printer.print(expr))
    }

    @Test
    fun printsSlashOperator() {
        val expr = Expr.Binary(
            Expr.Literal(10),
            Token(TokenType.SLASH, "/", null, 1),
            Expr.Literal(2)
        )
        assertEquals("(/ 10 2)", printer.print(expr))
    }

    @Test
    fun printsComplexArithmeticExpression() {
        // 1 + 2 * 3 (ohne Berücksichtigung von Operator-Präzedenz im AST)
        // Wird als (1 + (2 * 3)) dargestellt
        val expr = Expr.Binary(
            Expr.Literal(1),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Binary(
                Expr.Literal(2),
                Token(TokenType.STAR, "*", null, 1),
                Expr.Literal(3)
            )
        )
        assertEquals("(+ 1 (* 2 3))", printer.print(expr))
    }

    @Test
    fun printsDecimalNumbers() {
        val expr = Expr.Literal(3.14159)
        assertEquals("3.14159", printer.print(expr))
    }

    @Test
    fun printsBooleanLiterals() {
        val trueExpr = Expr.Literal(true)
        val falseExpr = Expr.Literal(false)

        assertEquals("true", printer.print(trueExpr))
        assertEquals("false", printer.print(falseExpr))
    }

    @Test
    fun printsGroupingWithBinary() {
        // (1 + 2)
        val expr = Expr.Grouping(
            Expr.Binary(
                Expr.Literal(1),
                Token(TokenType.PLUS, "+", null, 1),
                Expr.Literal(2)
            )
        )
        assertEquals("(group (+ 1 2))", printer.print(expr))
    }

    @Test
    fun printsVeryDeeplyNestedExpression() {
        // ((1 + 2) * 3) - (4 / 5)
        val expr = Expr.Binary(
            Expr.Binary(
                Expr.Grouping(
                    Expr.Binary(
                        Expr.Literal(1),
                        Token(TokenType.PLUS, "+", null, 1),
                        Expr.Literal(2)
                    )
                ),
                Token(TokenType.STAR, "*", null, 1),
                Expr.Literal(3)
            ),
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Grouping(
                Expr.Binary(
                    Expr.Literal(4),
                    Token(TokenType.SLASH, "/", null, 1),
                    Expr.Literal(5)
                )
            )
        )
        assertEquals("(- (* (group (+ 1 2)) 3) (group (/ 4 5)))", printer.print(expr))
    }
}
