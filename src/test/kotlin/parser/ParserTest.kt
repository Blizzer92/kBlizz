package parser

import at.kblizz.ErrorReporter
import at.kblizz.ast.Expr
import at.kblizz.parser.Parser
import at.kblizz.scanner.Scanner
import at.kblizz.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ParserTest {
    private fun parse(source: String): Expr? {
        val errorReporter = ErrorReporter()
        val tokens = Scanner(source, errorReporter).scanTokens()

        return Parser(tokens).parse()
    }

    @Test
    fun parsesNumberLiteral() {
        val expr = parse("123")

        val literal = assertIs<Expr.Literal>(assertNotNull(expr))
        assertEquals(123.0, literal.value)
    }

    @Test
    fun parsesStringLiteral() {
        val expr = parse("\"hello\"")

        val literal = assertIs<Expr.Literal>(assertNotNull(expr))
        assertEquals("hello", literal.value)
    }

    @Test
    fun parsesBooleanAndNilLiterals() {
        val trueExpr = assertIs<Expr.Literal>(assertNotNull(parse("true")))
        val falseExpr = assertIs<Expr.Literal>(assertNotNull(parse("false")))
        val nilExpr = assertIs<Expr.Literal>(assertNotNull(parse("nil")))

        assertEquals(true, trueExpr.value)
        assertEquals(false, falseExpr.value)
        assertNull(nilExpr.value)
    }

    @Test
    fun parsesGroupingExpression() {
        val expr = parse("(1)")

        val grouping = assertIs<Expr.Grouping>(assertNotNull(expr))
        val literal = assertIs<Expr.Literal>(grouping.expression)

        assertEquals(1.0, literal.value)
    }

    @Test
    fun parsesUnaryExpression() {
        val expr = parse("-123")

        val unary = assertIs<Expr.Unary>(assertNotNull(expr))
        val right = assertIs<Expr.Literal>(unary.right)

        assertEquals(TokenType.MINUS, unary.operator.type)
        assertEquals(123.0, right.value)
    }

    @Test
    fun parsesMultiplicationBeforeAddition() {
        val expr = parse("1 + 2 * 3")

        val addition = assertIs<Expr.Binary>(assertNotNull(expr))
        assertEquals(TokenType.PLUS, addition.operator.type)

        val left = assertIs<Expr.Literal>(addition.left)
        assertEquals(1.0, left.value)

        val multiplication = assertIs<Expr.Binary>(addition.right)
        assertEquals(TokenType.STAR, multiplication.operator.type)

        val multiplicationLeft = assertIs<Expr.Literal>(multiplication.left)
        val multiplicationRight = assertIs<Expr.Literal>(multiplication.right)

        assertEquals(2.0, multiplicationLeft.value)
        assertEquals(3.0, multiplicationRight.value)
    }

    @Test
    fun parsesComparisonBeforeEquality() {
        val expr = parse("1 < 2 == true")

        val equality = assertIs<Expr.Binary>(assertNotNull(expr))
        assertEquals(TokenType.EQUAL_EQUAL, equality.operator.type)

        val comparison = assertIs<Expr.Binary>(equality.left)
        assertEquals(TokenType.LESS, comparison.operator.type)

        val right = assertIs<Expr.Literal>(equality.right)
        assertEquals(true, right.value)
    }

    @Test
    fun parsesLeftAssociativeTermExpression() {
        val expr = parse("1 - 2 - 3")

        val outer = assertIs<Expr.Binary>(assertNotNull(expr))
        assertEquals(TokenType.MINUS, outer.operator.type)

        val inner = assertIs<Expr.Binary>(outer.left)
        assertEquals(TokenType.MINUS, inner.operator.type)

        val outerRight = assertIs<Expr.Literal>(outer.right)
        assertEquals(3.0, outerRight.value)
    }

    @Test
    fun returnsNullForInvalidExpression() {
        val expr = parse("* 123")

        assertNull(expr)
    }

    @Test
    fun returnsNullForMissingClosingParenthesis() {
        val expr = parse("(123")

        assertNull(expr)
    }
}