package at.kblizz.error

import at.kblizz.token.Token

class RuntimeError(val token: Token?, message: String?) : RuntimeException(message)