package at.kblizz

import at.kblizz.token.Token
import at.kblizz.token.TokenType



class ErrorReporter {
    var hadError = false
        private set

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    private fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error$where: $message")
        hadError = true
    }

    fun error(token: Token?, message: String?) {
        if (token == null) {
            report(0, " at unknown location", message!!)
            return
        }
        if (token.type === TokenType.EOF) {
            report(token.line, " at end", message!!)
        } else {
            report(token.line, " at '" + token.lexeme + "'", message!!)
        }
    }

    fun reset() {
        hadError = false
    }
}
