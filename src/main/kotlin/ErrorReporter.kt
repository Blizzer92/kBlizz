package at.kblizz

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

    fun reset() {
        hadError = false
    }
}
