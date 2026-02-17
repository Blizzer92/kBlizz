package at.kblizz

import at.kblizz.scanner.Scanner
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

class KBlizz {
    var hadError = false

    fun start(args: Array<String>) {
        if(args.count() > 1){
            println("Usage: kBlizz [script]")
            exitProcess(64);
        } else if(args.count() == 1){
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    fun runFile(path: String){
        val bytes = File(path).readBytes();
        run(String(bytes, Charset.defaultCharset()))

        if(hadError){
            exitProcess(65)
        }
    }

    fun runPrompt(){
        while(true) {
            println("> ")
            val input = readlnOrNull() ?: return
            run(input)
            hadError = false
        }
    }

    fun run(input: String){
        val scanner = Scanner(input)
        val tokens = scanner.scanTokens()

        for(token in tokens){
            println(token)
        }
    }

    fun error(line: Int, message: String?) {
        report(line, "", message)
    }

    private fun report(
        line: Int, where: String?,
        message: String?
    ) {
        System.err.println(
            "[line $line] Error$where: $message"
        )
        hadError = true
    }
}
