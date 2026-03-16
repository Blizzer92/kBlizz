package at.kblizz.tool

import java.io.IOException
import java.io.PrintWriter
import java.util.*
import kotlin.system.exitProcess


object GenerateAst {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        if (args.size != 1) {
            System.err.println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir: String? = args[0]

        defineAst(outputDir, "Expr", listOf(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Any value",
            "Unary    : Token operator, Expr right"
        ) as MutableList<String?>?
        );
    }

    @Throws(IOException::class)
    private fun defineAst(
        outputDir: String?, baseName: String?, types: MutableList<String?>?
    ) {
        val path = "$outputDir/$baseName.kt"
        val writer = PrintWriter(path, "UTF-8")

        writer.println("package at.kblizz.ast")
        writer.println()
        writer.println("import at.kblizz.token.Token")
        writer.println()
        writer.println("sealed class $baseName {")

        defineVisitor(writer, baseName, types as MutableList<String>)

        // The AST classes.
        for (type in types!!) {
            val className =
                type!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
            val fields = type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }
            defineType(writer, baseName, className, fields)
        }

        // The base accept() method.
        writer.println()
        writer.println("    abstract fun <R> accept(visitor: Visitor<R>): R")

        writer.println("}")
        writer.close()
    }

    private fun defineType(
        writer: PrintWriter, baseName: String?,
        className: String?, fieldList: String
    ) {
        // Convert field list to Kotlin syntax
        val fields = fieldList.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val kotlinFields = fields.joinToString(", ") { field ->
            val parts = field.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = parts[0]
            val name = parts[1]
            "val $name: $type"
        }

        writer.println("    data class $className($kotlinFields) : $baseName() {")

        // Visitor pattern.
        writer.println("        override fun <R> accept(visitor: Visitor<R>): R {")
        writer.println("            return visitor.visit$className$baseName(this)")
        writer.println("        }")

        writer.println("    }")
        writer.println()
    }

    private fun defineVisitor(
        writer: PrintWriter, baseName: String?, types: MutableList<String>
    ) {
        writer.println("  interface Visitor<R> {")

        for (type in types) {
            val typeName = type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
            writer.println(
                "    fun visit" + typeName + baseName + "(" +
                        baseName?.lowercase(Locale.getDefault()) + ": " + typeName + "): R"
            )
        }

        writer.println("  }")
    }

}