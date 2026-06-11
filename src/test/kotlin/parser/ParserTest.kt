package parser

import at.kblizz.ErrorReporter
import at.kblizz.ast.Expr
import at.kblizz.ast.Stmt
import at.kblizz.parser.Parser
import at.kblizz.scanner.Scanner
import at.kblizz.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParserTest {
    /** Parse a single expression (appends ';' so the parser accepts it as an expression statement). */
    private fun parseExpr(source: String): Expr? {
        val stmts = parseStmts("$source;")
        return (stmts.firstOrNull() as? Stmt.Expression)?.expression
    }

    private fun parseStmts(source: String): List<Stmt> {
        return Parser(Scanner(source, ErrorReporter()).scanTokens()).parse()
    }

    // --- expression parsing ---

    @Test
    fun parsesNumberLiteral() {
        val literal = assertIs<Expr.Literal>(assertNotNull(parseExpr("123")))
        assertEquals(123.0, literal.value)
    }

    @Test
    fun parsesStringLiteral() {
        val literal = assertIs<Expr.Literal>(assertNotNull(parseExpr("\"hello\"")))
        assertEquals("hello", literal.value)
    }

    @Test
    fun parsesBooleanAndNilLiterals() {
        val trueExpr = assertIs<Expr.Literal>(assertNotNull(parseExpr("true")))
        val falseExpr = assertIs<Expr.Literal>(assertNotNull(parseExpr("false")))
        val nilExpr = assertIs<Expr.Literal>(assertNotNull(parseExpr("nil")))

        assertEquals(true, trueExpr.value)
        assertEquals(false, falseExpr.value)
        assertNull(nilExpr.value)
    }

    @Test
    fun parsesGroupingExpression() {
        val grouping = assertIs<Expr.Grouping>(assertNotNull(parseExpr("(1)")))
        val literal = assertIs<Expr.Literal>(grouping.expression)
        assertEquals(1.0, literal.value)
    }

    @Test
    fun parsesUnaryExpression() {
        val unary = assertIs<Expr.Unary>(assertNotNull(parseExpr("-123")))
        val right = assertIs<Expr.Literal>(unary.right)
        assertEquals(TokenType.MINUS, unary.operator.type)
        assertEquals(123.0, right.value)
    }

    @Test
    fun parsesMultiplicationBeforeAddition() {
        val addition = assertIs<Expr.Binary>(assertNotNull(parseExpr("1 + 2 * 3")))
        assertEquals(TokenType.PLUS, addition.operator.type)

        val left = assertIs<Expr.Literal>(addition.left)
        assertEquals(1.0, left.value)

        val multiplication = assertIs<Expr.Binary>(addition.right)
        assertEquals(TokenType.STAR, multiplication.operator.type)

        assertEquals(2.0, assertIs<Expr.Literal>(multiplication.left).value)
        assertEquals(3.0, assertIs<Expr.Literal>(multiplication.right).value)
    }

    @Test
    fun parsesComparisonBeforeEquality() {
        val equality = assertIs<Expr.Binary>(assertNotNull(parseExpr("1 < 2 == true")))
        assertEquals(TokenType.EQUAL_EQUAL, equality.operator.type)

        val comparison = assertIs<Expr.Binary>(equality.left)
        assertEquals(TokenType.LESS, comparison.operator.type)

        assertEquals(true, assertIs<Expr.Literal>(equality.right).value)
    }

    @Test
    fun parsesLeftAssociativeTermExpression() {
        val outer = assertIs<Expr.Binary>(assertNotNull(parseExpr("1 - 2 - 3")))
        assertEquals(TokenType.MINUS, outer.operator.type)

        assertIs<Expr.Binary>(outer.left).also {
            assertEquals(TokenType.MINUS, it.operator.type)
        }

        assertEquals(3.0, assertIs<Expr.Literal>(outer.right).value)
    }

    @Test
    fun parsesVariableExpression() {
        val variable = assertIs<Expr.Variable>(assertNotNull(parseExpr("myVar")))
        assertEquals("myVar", variable.name.lexeme)
    }

    @Test
    fun parsesAssignmentExpression() {
        val stmts = parseStmts("x = 5;")
        val assign = assertIs<Expr.Assign>(assertIs<Stmt.Expression>(stmts[0]).expression)
        assertEquals("x", assign.name.lexeme)
        assertEquals(5.0, assertIs<Expr.Literal>(assign.value).value)
    }

    @Test
    fun returnsEmptyListForInvalidExpression() {
        assertTrue(parseStmts("* 123").isEmpty())
    }

    @Test
    fun returnsEmptyListForMissingClosingParenthesis() {
        assertTrue(parseStmts("(123").isEmpty())
    }

    // --- statement parsing ---

    @Test
    fun parsesPrintStatement() {
        val stmts = parseStmts("print 42;")
        assertEquals(1, stmts.size)
        val print = assertIs<Stmt.Print>(stmts[0])
        assertEquals(42.0, assertIs<Expr.Literal>(print.expression).value)
    }

    @Test
    fun parsesPrintWithStringLiteral() {
        val stmts = parseStmts("print \"hello\";")
        val print = assertIs<Stmt.Print>(stmts[0])
        assertEquals("hello", assertIs<Expr.Literal>(print.expression).value)
    }

    @Test
    fun parsesVarDeclarationWithInitializer() {
        val stmts = parseStmts("var x = 10;")
        val varStmt = assertIs<Stmt.Var>(stmts[0])
        assertEquals("x", varStmt.name.lexeme)
        assertEquals(10.0, assertIs<Expr.Literal>(assertNotNull(varStmt.initializer)).value)
    }

    @Test
    fun parsesVarDeclarationWithoutInitializer() {
        val stmts = parseStmts("var x;")
        val varStmt = assertIs<Stmt.Var>(stmts[0])
        assertEquals("x", varStmt.name.lexeme)
        assertNull(varStmt.initializer)
    }

    @Test
    fun parsesBlockStatement() {
        val stmts = parseStmts("{ print 1; print 2; }")
        val block = assertIs<Stmt.Block>(stmts[0])
        assertEquals(2, block.statements.size)
        assertIs<Stmt.Print>(block.statements[0])
        assertIs<Stmt.Print>(block.statements[1])
    }

    @Test
    fun parsesEmptyBlock() {
        val stmts = parseStmts("{}")
        val block = assertIs<Stmt.Block>(stmts[0])
        assertEquals(0, block.statements.size)
    }

    @Test
    fun parsesNestedBlocks() {
        val stmts = parseStmts("{ { print 1; } }")
        val outer = assertIs<Stmt.Block>(stmts[0])
        val inner = assertIs<Stmt.Block>(outer.statements[0])
        assertIs<Stmt.Print>(inner.statements[0])
    }

    @Test
    fun parsesMultipleStatements() {
        val stmts = parseStmts("var a = 1; var b = 2; print a;")
        assertEquals(3, stmts.size)
        assertIs<Stmt.Var>(stmts[0])
        assertIs<Stmt.Var>(stmts[1])
        assertIs<Stmt.Print>(stmts[2])
    }
}
