package at.kblizz

import at.kblizz.scanner.Scanner
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

class KBlizz {
    private val errorReporter = ErrorReporter()

    companion object {
        private const val EXIT_CODE_USAGE = 64
        private const val EXIT_CODE_DATA_ERROR = 65
    }

    fun start(args: Array<String>) {
        if (args.count() > 1) {
            println("Usage: kBlizz [script]")
            exitProcess(EXIT_CODE_USAGE)
        } else if (args.count() == 1) {
            runFile(args[0])
        } else {
            runPrompt()
        }
    }

    fun runFile(path: String) {
        val bytes = File(path).readBytes()
        run(String(bytes, Charset.defaultCharset()))

        if (errorReporter.hadError) {
            exitProcess(EXIT_CODE_DATA_ERROR)
        }
    }

    fun runPrompt() {
        while (true) {
            print("> ")
            val input = readlnOrNull() ?: return
            run(input)
            errorReporter.reset()
        }
    }

    fun run(input: String) {
        val scanner = Scanner(input, errorReporter)
        val tokens = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }
}
