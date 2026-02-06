package at.kblizz.scanner

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
}
