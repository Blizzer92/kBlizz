package at.kblizz.environment

import at.kblizz.error.RuntimeError
import at.kblizz.token.Token


class Environment(var enclosing: Environment? = null) {

    private val values: MutableMap<String, Any?> = HashMap()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }

        enclosing?.let { return it.get(name) }

        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        if (enclosing != null) {
            enclosing!!.assign(name, value)
            return
        }

        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }
}