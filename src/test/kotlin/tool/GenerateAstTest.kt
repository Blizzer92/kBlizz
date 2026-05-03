package tool

import at.kblizz.tool.GenerateAst
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class GenerateAstTest {
    private val testOutputDir = "build/test-generated"

    @BeforeTest
    fun setup() {
        File(testOutputDir).mkdirs()
    }

    @AfterTest
    fun cleanup() {
        File(testOutputDir).deleteRecursively()
    }

    @Test
    fun generatesExprClassFile() {
        GenerateAst.main(arrayOf(testOutputDir))

        val generatedFile = File("$testOutputDir/Expr.kt")
        assertTrue(generatedFile.exists(), "Expr.kt should be generated")
    }

    @Test
    fun generatedFileContainsPackageDeclaration() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("package at.kblizz.ast"), "Generated file should contain package declaration")
    }

    @Test
    fun generatedFileContainsImports() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("import at.kblizz.token.Token"), "Generated file should import Token class")
    }

    @Test
    fun generatedFileContainsSealedClass() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("sealed class Expr"), "Generated file should contain sealed class Expr")
    }

    @Test
    fun generatedFileContainsVisitorInterface() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("interface Visitor<R>"), "Generated file should contain Visitor interface")
    }

    @Test
    fun generatedFileContainsBinaryClass() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("data class Binary"), "Generated file should contain Binary data class")
        assertTrue(
            content.contains("val left: Expr") && content.contains("val operator: Token") && content.contains("val right: Expr"),
            "Binary class should have correct properties"
        )
    }

    @Test
    fun generatedFileContainsGroupingClass() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("data class Grouping"), "Generated file should contain Grouping data class")
        assertTrue(content.contains("val expression: Expr"), "Grouping class should have expression property")
    }

    @Test
    fun generatedFileContainsLiteralClass() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("data class Literal"), "Generated file should contain Literal data class")
        assertTrue(content.contains("val value: Any"), "Literal class should have value property")
    }

    @Test
    fun generatedFileContainsUnaryClass() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("data class Unary"), "Generated file should contain Unary data class")
        assertTrue(
            content.contains("val operator: Token") && content.contains("val right: Expr"),
            "Unary class should have correct properties"
        )
    }

    @Test
    fun generatedClassesInheritFromBaseClass() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()"))
        assertTrue(content.contains("Grouping(val expression: Expr) : Expr()"))
        assertTrue(content.contains("Literal(val value: Any?) : Expr()"))
        assertTrue(content.contains("Unary(val operator: Token, val right: Expr) : Expr()"))
    }

    @Test
    fun generatedClassesImplementAcceptMethod() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("override fun <R> accept(visitor: Visitor<R>): R"))
    }

    @Test
    fun visitorInterfaceContainsAllVisitMethods() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()
        assertTrue(content.contains("fun visitBinaryExpr(expr: Binary): R"))
        assertTrue(content.contains("fun visitGroupingExpr(expr: Grouping): R"))
        assertTrue(content.contains("fun visitLiteralExpr(expr: Literal): R"))
        assertTrue(content.contains("fun visitUnaryExpr(expr: Unary): R"))
    }

    @Test
    fun generatedFileHasCorrectStructure() {
        GenerateAst.main(arrayOf(testOutputDir))

        val content = File("$testOutputDir/Expr.kt").readText()

        // Check order: package, imports, sealed class, visitor, data classes, abstract accept
        val packageIndex = content.indexOf("package at.kblizz.ast")
        val importIndex = content.indexOf("import at.kblizz.token.Token")
        val sealedClassIndex = content.indexOf("sealed class Expr")
        val visitorIndex = content.indexOf("interface Visitor<R>")
        val binaryIndex = content.indexOf("data class Binary")
        val abstractAcceptIndex = content.indexOf("abstract fun <R> accept(visitor: Visitor<R>): R")

        assertTrue(packageIndex < importIndex, "Package should come before imports")
        assertTrue(importIndex < sealedClassIndex, "Imports should come before sealed class")
        assertTrue(sealedClassIndex < visitorIndex, "Sealed class should come before visitor")
        assertTrue(visitorIndex < binaryIndex, "Visitor should come before data classes")
        assertTrue(binaryIndex < abstractAcceptIndex, "Data classes should come before abstract accept method")
    }
}
