package scanner

import at.kblizz.scanner.Scanner
import at.kblizz.token.Token
import at.kblizz.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals

class ScannerTest {
    private fun scanTokens(source: String): List<Token> {
        return Scanner(source).scanTokens().filterNotNull()
    }

    private fun assertTypes(source: String, expected: List<TokenType>) {
        val types = scanTokens(source).map { it.type!! }
        assertEquals(expected, types)
    }

    @Test
    fun scansSingleCharacterTokens() {
        assertTypes(
            "(){}.,-+;*",
            listOf(
                TokenType.LEFT_PARENT,
                TokenType.RIGHT_PARENT,
                TokenType.LEFT_BRACED,
                TokenType.RIGHT_BRACED,
                TokenType.DOT,
                TokenType.COMMA,
                TokenType.MINUS,
                TokenType.PLUS,
                TokenType.SEMICOLON,
                TokenType.STAR,
                TokenType.EOF
            )
        )
    }

    @Test
    fun scansBangAndEqualsOperators() {
        assertTypes("!", listOf(TokenType.BANG, TokenType.EOF))
        assertTypes("!=", listOf(TokenType.BANG_EQUAL, TokenType.EOF))
        assertTypes("=", listOf(TokenType.EQUAL, TokenType.EOF))
        assertTypes("==", listOf(TokenType.EQUAL_EQUAL, TokenType.EOF))
    }

    @Test
    fun scansComparisonOperators() {
        assertTypes("<", listOf(TokenType.LESS, TokenType.EOF))
        assertTypes("<=", listOf(TokenType.LESS_EQUAL, TokenType.EOF))
        assertTypes(">", listOf(TokenType.GREATER, TokenType.EOF))
        assertTypes(">=", listOf(TokenType.GREATER_EQUAL, TokenType.EOF))
    }

    @Test
    fun keepsLexemesForMultiCharacterTokens() {
        val tokens = scanTokens("!=")
        assertEquals(TokenType.BANG_EQUAL, tokens[0].type)
        assertEquals("!=", tokens[0].lexeme)
        assertEquals(TokenType.EOF, tokens[1].type)
    }

    @Test
    fun ignoresUnknownCharactersExceptForErrorReporting() {
        assertTypes("@", listOf(TokenType.EOF))
    }

    @Test
    fun scansSlashAndComments() {
        assertTypes("/", listOf(TokenType.SLASH, TokenType.EOF))
        assertTypes("// this is a comment", listOf(TokenType.EOF))
        assertTypes("// comment\n+", listOf(TokenType.PLUS, TokenType.EOF))
    }

    @Test
    fun ignoresWhitespace() {
        assertTypes("  \t\r\n  +  ", listOf(TokenType.PLUS, TokenType.EOF))
    }

    @Test
    fun scansStringLiterals() {
        val tokens = scanTokens("\"hello\"")
        assertEquals(TokenType.STRING, tokens[0].type)
        assertEquals("\"hello\"", tokens[0].lexeme)
        assertEquals("hello", tokens[0].literal)
    }

    @Test
    fun scansMultilineStrings() {
        val tokens = scanTokens("\"hello\nworld\"")
        assertEquals(TokenType.STRING, tokens[0].type)
        assertEquals("hello\nworld", tokens[0].literal)
    }

    @Test
    fun tracksLineNumbersInStrings() {
        val tokens = scanTokens("+\n\"test\nstring\"\n*")
        assertEquals(1, tokens[0].line)
        assertEquals(3, tokens[1].line)
        assertEquals(4, tokens[2].line)
    }

    @Test
    fun tracksLineNumbersAcrossMultipleLines() {
        val tokens = scanTokens("+\n-\n*")
        assertEquals(1, tokens[0].line)
        assertEquals(2, tokens[1].line)
        assertEquals(3, tokens[2].line)
    }

    @Test
    fun handlesCommentsAtEndOfLine() {
        assertTypes("+ // comment", listOf(TokenType.PLUS, TokenType.EOF))
    }

    @Test
    fun handlesMultipleOperatorsWithComments() {
        assertTypes("+ - // comment\n*", listOf(TokenType.PLUS, TokenType.MINUS, TokenType.STAR, TokenType.EOF))
    }

    @Test
    fun scansNumbers() {
        val tokens = scanTokens("123")
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("123", tokens[0].lexeme)
        assertEquals(123.0, tokens[0].literal)
    }

    @Test
    fun scansDecimalNumbers() {
        val tokens = scanTokens("123.456")
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("123.456", tokens[0].lexeme)
        assertEquals(123.456, tokens[0].literal)
    }

    @Test
    fun scansNumbersWithoutTrailingDecimal() {
        val tokens = scanTokens("123.")
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("123", tokens[0].lexeme)
        assertEquals(123.0, tokens[0].literal)
        assertEquals(TokenType.DOT, tokens[1].type)
    }

    @Test
    fun scansNumberStartingWithZero() {
        val tokens = scanTokens("0.5")
        assertEquals(TokenType.NUMBER, tokens[0].type)
        assertEquals("0.5", tokens[0].lexeme)
        assertEquals(0.5, tokens[0].literal)
    }

    @Test
    fun scansIdentifiers() {
        val tokens = scanTokens("abc")
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("abc", tokens[0].lexeme)
    }

    @Test
    fun scansIdentifiersWithUnderscores() {
        val tokens = scanTokens("_test_var")
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("_test_var", tokens[0].lexeme)
    }

    @Test
    fun scansIdentifiersWithNumbers() {
        val tokens = scanTokens("var123")
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("var123", tokens[0].lexeme)
    }

    @Test
    fun scansKeywords() {
        assertTypes("and", listOf(TokenType.AND, TokenType.EOF))
        assertTypes("class", listOf(TokenType.CLASS, TokenType.EOF))
        assertTypes("else", listOf(TokenType.ELSE, TokenType.EOF))
        assertTypes("false", listOf(TokenType.FALSE, TokenType.EOF))
        assertTypes("for", listOf(TokenType.FOR, TokenType.EOF))
        assertTypes("fun", listOf(TokenType.FUN, TokenType.EOF))
        assertTypes("if", listOf(TokenType.IF, TokenType.EOF))
        assertTypes("nil", listOf(TokenType.NIL, TokenType.EOF))
        assertTypes("or", listOf(TokenType.OR, TokenType.EOF))
        assertTypes("print", listOf(TokenType.PRINT, TokenType.EOF))
        assertTypes("return", listOf(TokenType.RETURN, TokenType.EOF))
        assertTypes("super", listOf(TokenType.SUPER, TokenType.EOF))
        assertTypes("this", listOf(TokenType.THIS, TokenType.EOF))
        assertTypes("true", listOf(TokenType.TRUE, TokenType.EOF))
        assertTypes("var", listOf(TokenType.VAR, TokenType.EOF))
        assertTypes("while", listOf(TokenType.WHILE, TokenType.EOF))
    }

    @Test
    fun distinguishesKeywordsFromIdentifiers() {
        assertTypes("variable", listOf(TokenType.IDENTIFIER, TokenType.EOF))
        assertTypes("vars", listOf(TokenType.IDENTIFIER, TokenType.EOF))
        assertTypes("varx", listOf(TokenType.IDENTIFIER, TokenType.EOF))
        assertTypes("xvar", listOf(TokenType.IDENTIFIER, TokenType.EOF))
    }

    @Test
    fun scansComplexExpression() {
        assertTypes(
            "var x = 123.45;",
            listOf(
                TokenType.VAR,
                TokenType.IDENTIFIER,
                TokenType.EQUAL,
                TokenType.NUMBER,
                TokenType.SEMICOLON,
                TokenType.EOF
            )
        )
    }

    @Test
    fun scansFunctionDeclaration() {
        assertTypes(
            "fun add(a, b) { return a + b; }",
            listOf(
                TokenType.FUN,
                TokenType.IDENTIFIER,
                TokenType.LEFT_PARENT,
                TokenType.IDENTIFIER,
                TokenType.COMMA,
                TokenType.IDENTIFIER,
                TokenType.RIGHT_PARENT,
                TokenType.LEFT_BRACED,
                TokenType.RETURN,
                TokenType.IDENTIFIER,
                TokenType.PLUS,
                TokenType.IDENTIFIER,
                TokenType.SEMICOLON,
                TokenType.RIGHT_BRACED,
                TokenType.EOF
            )
        )
    }

    @Test
    fun scansIfStatement() {
        assertTypes(
            "if (x > 10) print true; else print false;",
            listOf(
                TokenType.IF,
                TokenType.LEFT_PARENT,
                TokenType.IDENTIFIER,
                TokenType.GREATER,
                TokenType.NUMBER,
                TokenType.RIGHT_PARENT,
                TokenType.PRINT,
                TokenType.TRUE,
                TokenType.SEMICOLON,
                TokenType.ELSE,
                TokenType.PRINT,
                TokenType.FALSE,
                TokenType.SEMICOLON,
                TokenType.EOF
            )
        )
    }

    @Test
    fun scansLogicalOperators() {
        assertTypes(
            "x and y or z",
            listOf(
                TokenType.IDENTIFIER,
                TokenType.AND,
                TokenType.IDENTIFIER,
                TokenType.OR,
                TokenType.IDENTIFIER,
                TokenType.EOF
            )
        )
    }

    @Test
    fun scansMixedContent() {
        val source = """
            // Variable declaration
            var count = 42;
            if (count >= 10) {
                print "Large";
            }
        """.trimIndent()

        val tokens = scanTokens(source)
        assertEquals(TokenType.VAR, tokens[0].type)
        assertEquals(TokenType.IDENTIFIER, tokens[1].type)
        assertEquals("count", tokens[1].lexeme)
        assertEquals(TokenType.EQUAL, tokens[2].type)
        assertEquals(TokenType.NUMBER, tokens[3].type)
        assertEquals(42.0, tokens[3].literal)
    }
}
