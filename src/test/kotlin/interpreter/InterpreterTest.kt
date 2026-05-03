package interpreter

import at.kblizz.ErrorReporter
import at.kblizz.interpreter.Interpreter
import at.kblizz.parser.Parser
import at.kblizz.scanner.Scanner
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InterpreterTest {
    private fun interpret(source: String): CapturedOutput {
        val expression = parse(source)
        val interpreter: Interpreter = Interpreter()

        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()

        val originalOut = System.out
        val originalErr = System.err

        try {
            System.setOut(PrintStream(stdout))
            System.setErr(PrintStream(stderr))

            interpreter.interpret(expression)

            return CapturedOutput(
                stdout = stdout.toString().trim(),
                stderr = stderr.toString().trim()
            )
        } finally {
            System.setOut(originalOut)
            System.setErr(originalErr)
        }
    }

    private fun parse(source: String) =
        Parser(
            Scanner(source, ErrorReporter()).scanTokens()
        ).parse()

    @Test
    fun interpretsNumberLiteral() {
        val output = interpret("123")

        assertEquals("123", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun interpretsStringLiteral() {
        val output = interpret("\"hello\"")

        assertEquals("hello", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun interpretsNilLiteral() {
        val output = interpret("nil")

        assertEquals("nil", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun interpretsArithmeticExpression() {
        val output = interpret("1 + 2 * 3 - 4 / 2")

        assertEquals("5", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun interpretsGroupingExpression() {
        val output = interpret("(1 + 2) * 3")

        assertEquals("9", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun interpretsUnaryMinus() {
        val output = interpret("-123")

        assertEquals("-123", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun interpretsBangOperator() {
        assertEquals("false", interpret("!true").stdout)
        assertEquals("true", interpret("!false").stdout)
        assertEquals("true", interpret("!nil").stdout)
        assertEquals("false", interpret("!123").stdout)
    }

    @Test
    fun interpretsComparisonOperators() {
        assertEquals("true", interpret("2 > 1").stdout)
        assertEquals("true", interpret("2 >= 2").stdout)
        assertEquals("true", interpret("1 < 2").stdout)
        assertEquals("true", interpret("2 <= 2").stdout)
    }

    @Test
    fun interpretsEqualityOperators() {
        assertEquals("true", interpret("1 == 1").stdout)
        assertEquals("false", interpret("1 == 2").stdout)
        assertEquals("true", interpret("1 != 2").stdout)
        assertEquals("true", interpret("nil == nil").stdout)
        assertEquals("false", interpret("nil == false").stdout)
    }

    @Test
    fun interpretsStringConcatenation() {
        val output = interpret("\"hello\" + \" world\"")

        assertEquals("hello world", output.stdout)
        assertEquals("", output.stderr)
    }

    @Test
    fun reportsRuntimeErrorForInvalidUnaryOperand() {
        val output = interpret("-\"hello\"")

        assertEquals("", output.stdout)
        assertTrue(output.stderr.contains("Operand must be a number."))
        assertTrue(output.stderr.contains("[line 1]"))
    }

    @Test
    fun reportsRuntimeErrorForInvalidBinaryOperands() {
        val output = interpret("1 - \"hello\"")

        assertEquals("", output.stdout)
        assertTrue(output.stderr.contains("Operands must be numbers."))
        assertTrue(output.stderr.contains("[line 1]"))
    }

    @Test
    fun reportsRuntimeErrorForInvalidPlusOperands() {
        val output = interpret("1 + \"hello\"")

        assertEquals("", output.stdout)
        assertTrue(output.stderr.contains("Operands must be two numbers or two strings."))
        assertTrue(output.stderr.contains("[line 1]"))
    }

    private data class CapturedOutput(
        val stdout: String,
        val stderr: String
    )
}