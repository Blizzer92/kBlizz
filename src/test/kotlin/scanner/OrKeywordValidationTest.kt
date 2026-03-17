package scanner

import at.kblizz.ErrorReporter
import at.kblizz.scanner.Scanner
import at.kblizz.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Validation test to ensure 'or' keyword works correctly without special case handling.
 * This test validates the refactoring that removed the 'o' -> match('r') hack.
 */
class OrKeywordValidationTest {

    @Test
    fun scansOrKeywordAlone() {
        val scanner = Scanner("or", ErrorReporter())
        val tokens = scanner.scanTokens()

        assertEquals(2, tokens.size, "Should have 2 tokens: OR and EOF")
        assertEquals(TokenType.OR, tokens[0].type)
        assertEquals("or", tokens[0].lexeme)
        assertEquals(TokenType.EOF, tokens[1].type)
    }

    @Test
    fun scansOrInExpression() {
        val scanner = Scanner("true or false", ErrorReporter())
        val tokens = scanner.scanTokens()

        assertEquals(4, tokens.size)
        assertEquals(TokenType.TRUE, tokens[0].type)
        assertEquals(TokenType.OR, tokens[1].type)
        assertEquals("or", tokens[1].lexeme)
        assertEquals(TokenType.FALSE, tokens[2].type)
        assertEquals(TokenType.EOF, tokens[3].type)
    }

    @Test
    fun distinguishesOrFromIdentifiersStartingWithO() {
        val scanner = Scanner("orange or orbit", ErrorReporter())
        val tokens = scanner.scanTokens()

        assertEquals(4, tokens.size)
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals("orange", tokens[0].lexeme)
        assertEquals(TokenType.OR, tokens[1].type)
        assertEquals("or", tokens[1].lexeme)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals("orbit", tokens[2].lexeme)
    }

    @Test
    fun scansMultipleOrKeywords() {
        val scanner = Scanner("a or b or c", ErrorReporter())
        val tokens = scanner.scanTokens()

        assertEquals(6, tokens.size)
        assertEquals(TokenType.IDENTIFIER, tokens[0].type)
        assertEquals(TokenType.OR, tokens[1].type)
        assertEquals(TokenType.IDENTIFIER, tokens[2].type)
        assertEquals(TokenType.OR, tokens[3].type)
        assertEquals(TokenType.IDENTIFIER, tokens[4].type)
        assertEquals(TokenType.EOF, tokens[5].type)
    }
}
