package at.kblizz.scanner

import at.kblizz.KBlizz
import at.kblizz.token.Token
import at.kblizz.token.TokenType


internal class Scanner(private val source: String?) {
    private val tokens: MutableList<Token?> = ArrayList()
    private val keywords = mapOf(
        "and" to TokenType.AND,
        "class" to TokenType.CLASS,
        "else" to TokenType.ELSE,
        "false" to TokenType.FALSE,
        "for" to TokenType.FOR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "nil" to TokenType.NIL,
        "or" to TokenType.OR,
        "print" to TokenType.PRINT,
        "return" to TokenType.RETURN,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "true" to TokenType.TRUE,
        "var" to TokenType.VAR,
        "while" to TokenType.WHILE

    )
    private var start = 0
    private var current = 0
    private var line = 1

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
            '/' -> {
                if(match('/')){
                    while (peek() != '\n' && !isAtEnd())
                    {
                        advance()
                    }
                }
                else{
                    addToken(TokenType.SLASH)
                }
            }
            ' ' -> {
            }
            '\r' -> {
            }
            '\t' -> {
            }
            '\n' -> {
                line++
            }
            '"' -> {
                string()
            }
            'o' -> {
                if(match('r')){
                    addToken(TokenType.OR)
                }
            }
            else -> {
                if(isDigit(c)){
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                }
                else {
                    KBlizz().error(line, "Unexpected character.")
                }
            }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) {
            advance()
        }

        val text = source!!.substring(start, current)
        var type = keywords[text]
        if (type == null) {
            type = TokenType.IDENTIFIER
        }
        addToken(type)
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
        if (isAtEnd()) {
            return false
        }
        if (source!!.get(current) != expected) {
            return false
        }

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) {
            return '\u0000'
        }
        return source!![current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source!!.length) {
            return '\u0000'
        }
        return source.get(current + 1)
    }

    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') ||
                (c in 'A'..'Z') || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            KBlizz().error(line, "Unterminated string.")
            return
        }

        advance()

        addToken(TokenType.STRING, source!!.substring(start + 1, current - 1))
    }

    private fun number() {
        while (isDigit(peek())) {
            advance()
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance()

            while (isDigit(peek())) {
                advance()
            }
        }

        addToken(
            TokenType.NUMBER,
            source!!.substring(start, current).toDouble()
        )
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }
}