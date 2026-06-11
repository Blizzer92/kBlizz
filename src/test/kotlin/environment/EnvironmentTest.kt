package environment

import at.kblizz.environment.Environment
import at.kblizz.error.RuntimeError
import at.kblizz.token.Token
import at.kblizz.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class EnvironmentTest {
    private fun token(name: String) = Token(TokenType.IDENTIFIER, name, null, 1)

    @Test
    fun definesAndGetsVariable() {
        val env = Environment()
        env.define("x", 42.0)
        assertEquals(42.0, env.get(token("x")))
    }

    @Test
    fun definesVariableWithNullValue() {
        val env = Environment()
        env.define("count", null)
        assertNull(env.get(token("count")))
    }

    @Test
    fun redefinitionOverwritesValue() {
        val env = Environment()
        env.define("score", 1.0)
        env.define("score", 2.0)
        assertEquals(2.0, env.get(token("score")))
    }

    @Test
    fun assignUpdatesValue() {
        val env = Environment()
        env.define("x", 1.0)
        env.assign(token("x"), 2.0)
        assertEquals(2.0, env.get(token("x")))
    }

    @Test
    fun getUndefinedVariableThrows() {
        assertFailsWith<RuntimeError> { Environment().get(token("missing")) }
    }

    @Test
    fun assignUndefinedVariableThrows() {
        assertFailsWith<RuntimeError> { Environment().assign(token("unknown"), 1.0) }
    }

    @Test
    fun innerScopeSeesOuterVariable() {
        val outer = Environment()
        outer.define("greeting", "outer")
        val inner = Environment(outer)
        assertEquals("outer", inner.get(token("greeting")))
    }

    @Test
    fun innerScopeShadowsOuterVariable() {
        val outer = Environment()
        outer.define("x", "outer")
        val inner = Environment(outer)
        inner.define("x", "inner")
        assertEquals("inner", inner.get(token("x")))
        assertEquals("outer", outer.get(token("x")))
    }

    @Test
    fun assignInInnerScopeModifiesOuterWhenNotShadowed() {
        val outer = Environment()
        outer.define("shared", "before")
        val inner = Environment(outer)
        inner.assign(token("shared"), "after")
        assertEquals("after", outer.get(token("shared")))
    }

    @Test
    fun innerVariableNotVisibleInOuterScope() {
        val outer = Environment()
        val inner = Environment(outer)
        inner.define("local", "inner only")
        assertFailsWith<RuntimeError> { outer.get(token("local")) }
    }

    @Test
    fun threeLayerScopeLookup() {
        val global = Environment()
        global.define("x", "global")
        val middle = Environment(global)
        val inner = Environment(middle)
        assertEquals("global", inner.get(token("x")))
    }
}
