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
        val statements = parse(source)
        val interpreter = Interpreter()

        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val originalOut = System.out
        val originalErr = System.err

        try {
            System.setOut(PrintStream(stdout))
            System.setErr(PrintStream(stderr))
            interpreter.interpret(statements)
            return CapturedOutput(
                stdout = stdout.toString().replace("\r\n", "\n").replace("\r", "\n").trim(),
                stderr = stderr.toString().replace("\r\n", "\n").replace("\r", "\n").trim()
            )
        } finally {
            System.setOut(originalOut)
            System.setErr(originalErr)
        }
    }

    private fun parse(source: String) =
        Parser(Scanner(source, ErrorReporter()).scanTokens()).parse()

    // --- expression evaluation (via print) ---

    @Test
    fun interpretsNumberLiteral() {
        assertEquals("123", interpret("print 123;").stdout)
    }

    @Test
    fun interpretsStringLiteral() {
        assertEquals("hello", interpret("print \"hello\";").stdout)
    }

    @Test
    fun interpretsNilLiteral() {
        assertEquals("nil", interpret("print nil;").stdout)
    }

    @Test
    fun interpretsArithmeticExpression() {
        assertEquals("5", interpret("print 1 + 2 * 3 - 4 / 2;").stdout)
    }

    @Test
    fun interpretsGroupingExpression() {
        assertEquals("9", interpret("print (1 + 2) * 3;").stdout)
    }

    @Test
    fun interpretsUnaryMinus() {
        assertEquals("-123", interpret("print -123;").stdout)
    }

    @Test
    fun interpretsBangOperator() {
        assertEquals("false", interpret("print !true;").stdout)
        assertEquals("true", interpret("print !false;").stdout)
        assertEquals("true", interpret("print !nil;").stdout)
        assertEquals("false", interpret("print !123;").stdout)
    }

    @Test
    fun interpretsComparisonOperators() {
        assertEquals("true", interpret("print 2 > 1;").stdout)
        assertEquals("true", interpret("print 2 >= 2;").stdout)
        assertEquals("true", interpret("print 1 < 2;").stdout)
        assertEquals("true", interpret("print 2 <= 2;").stdout)
    }

    @Test
    fun interpretsEqualityOperators() {
        assertEquals("true", interpret("print 1 == 1;").stdout)
        assertEquals("false", interpret("print 1 == 2;").stdout)
        assertEquals("true", interpret("print 1 != 2;").stdout)
        assertEquals("true", interpret("print nil == nil;").stdout)
        assertEquals("false", interpret("print nil == false;").stdout)
    }

    @Test
    fun interpretsStringConcatenation() {
        assertEquals("hello world", interpret("print \"hello\" + \" world\";").stdout)
    }

    // --- runtime errors ---

    @Test
    fun reportsRuntimeErrorForInvalidUnaryOperand() {
        val output = interpret("-\"hello\";")
        assertEquals("", output.stdout)
        assertTrue(output.stderr.contains("Operand must be a number."))
        assertTrue(output.stderr.contains("[line 1]"))
    }

    @Test
    fun reportsRuntimeErrorForInvalidBinaryOperands() {
        val output = interpret("1 - \"hello\";")
        assertEquals("", output.stdout)
        assertTrue(output.stderr.contains("Operands must be numbers."))
        assertTrue(output.stderr.contains("[line 1]"))
    }

    @Test
    fun reportsRuntimeErrorForInvalidPlusOperands() {
        val output = interpret("1 + \"hello\";")
        assertEquals("", output.stdout)
        assertTrue(output.stderr.contains("Operands must be two numbers or two strings."))
        assertTrue(output.stderr.contains("[line 1]"))
    }

    // --- print statement ---

    @Test
    fun printStatementOutputsNumber() {
        assertEquals("42", interpret("print 42;").stdout)
    }

    @Test
    fun printStatementOutputsBooleans() {
        assertEquals("true", interpret("print true;").stdout)
        assertEquals("false", interpret("print false;").stdout)
    }

    @Test
    fun printStatementOutputsNil() {
        assertEquals("nil", interpret("print nil;").stdout)
    }

    @Test
    fun multiplePrintStatements() {
        assertEquals("1\n2\n3", interpret("print 1;\nprint 2;\nprint 3;").stdout)
    }

    @Test
    fun printStripsTrailingDotZeroFromDoubles() {
        assertEquals("5", interpret("print 5.0;").stdout)
        assertEquals("3.14", interpret("print 3.14;").stdout)
    }

    // --- var declaration ---

    @Test
    fun varDeclarationWithInitializer() {
        assertEquals("10", interpret("var x = 10; print x;").stdout)
    }

    @Test
    fun varDeclarationWithoutInitializerIsNil() {
        assertEquals("nil", interpret("var x; print x;").stdout)
    }

    @Test
    fun varDeclarationWithExpression() {
        assertEquals("7", interpret("var x = 3 + 4; print x;").stdout)
    }

    @Test
    fun multipleVarDeclarations() {
        assertEquals("3", interpret("var a = 1; var b = 2; print a + b;").stdout)
    }

    // --- assignment ---

    @Test
    fun assignmentUpdatesVariable() {
        assertEquals("2", interpret("var x = 1; x = 2; print x;").stdout)
    }

    @Test
    fun assignmentReturnsValue() {
        assertEquals("5", interpret("var x; print x = 5;").stdout)
    }

    @Test
    fun assignmentToUndefinedVariableErrors() {
        assertTrue(interpret("x = 1;").stderr.contains("Undefined variable 'x'."))
    }

    @Test
    fun undefinedVariableAccessErrors() {
        assertTrue(interpret("print x;").stderr.contains("Undefined variable 'x'."))
    }

    // --- block scoping ---

    @Test
    fun blockExecutesStatements() {
        assertEquals("1\n2", interpret("{ print 1; print 2; }").stdout)
    }

    @Test
    fun blockCreatesNewScope() {
        val output = interpret("""
            var a = "outer";
            {
                var a = "inner";
                print a;
            }
            print a;
        """.trimIndent())
        assertEquals("inner\nouter", output.stdout)
    }

    @Test
    fun blockCanAccessOuterVariable() {
        val output = interpret("""
            var a = "outer";
            {
                print a;
            }
        """.trimIndent())
        assertEquals("outer", output.stdout)
    }

    @Test
    fun blockCanModifyOuterVariable() {
        val output = interpret("""
            var a = "before";
            {
                a = "after";
            }
            print a;
        """.trimIndent())
        assertEquals("after", output.stdout)
    }

    @Test
    fun innerScopeVariableNotVisibleAfterBlock() {
        val output = interpret("""
            var a = "outer";
            {
                var a = "inner";
            }
            print a;
        """.trimIndent())
        assertEquals("outer", output.stdout)
    }

    @Test
    fun nestedBlocksWithShadowing() {
        val output = interpret("""
            var a = "global a";
            var b = "global b";
            var c = "global c";
            {
              var a = "outer a";
              var b = "outer b";
              {
                var a = "inner a";
                print a;
                print b;
                print c;
              }
              print a;
              print b;
              print c;
            }
            print a;
            print b;
            print c;
        """.trimIndent())
        assertEquals(
            "inner a\nouter b\nglobal c\nouter a\nouter b\nglobal c\nglobal a\nglobal b\nglobal c",
            output.stdout
        )
    }

    private data class CapturedOutput(val stdout: String, val stderr: String)
}
