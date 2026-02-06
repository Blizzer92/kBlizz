package at.kblizz.scanner

import at.kblizz.KBlizz
import at.kblizz.token.Token
import at.kblizz.token.TokenType


internal class Scanner(private val source: String?) {
    private val tokens: MutableList<Token?> = ArrayList()
    private var start = 0
    private var current = 0
    private val line = 1

    fun scanTokens(): MutableList<Token?> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun isAtEnd(): Boolean {
        return current >= source!!.length
    }

    private fun scanToken() {
        val c: Char = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PARENT)
            ')' -> addToken(TokenType.RIGHT_PARENT)
            '{' -> addToken(TokenType.LEFT_BRACED)
            '}' -> addToken(TokenType.RIGHT_BRACED)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> {
                    if(match('=')){
                        addToken(TokenType.BANG_EQUAL)
                    }
                    else{
                        addToken(TokenType.BANG)
                    }
            }
            '=' -> {
                if(match('=')){
                    addToken(TokenType.EQUAL_EQUAL)
                }
                else{
                    addToken(TokenType.EQUAL)
                }
            }
            '<' -> {
                if(match('=')){
                    addToken(TokenType.LESS_EQUAL)
                }
                else{
                    addToken(TokenType.LESS)
                }
            }
            '>' -> {
                if(match('=')){
                    addToken(TokenType.GREATER_EQUAL)
                }
                else{
                    addToken(TokenType.GREATER)
                }
            }
            else -> {
                KBlizz().error(line, "Unexpected character.")
            }

        }
    }

    private fun advance(): Char {
        return source!![current++]
    }

    private fun addToken(type: TokenType?) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType?, literal: Any?) {
        val text = source!!.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source!!.get(current) != expected) return false

        current++
        return true
    }
}