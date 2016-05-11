package y2k.joyreactor.kotlin

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by y2k on 5/11/16.
 */
class TryCacheTest {

    @Test
    fun test() {
        val actual = try {
            "SUCCESS".toString()
        } catch (e: Exception) {
            "FAIL".toString()
        }
        assertEquals("SUCCESS", actual)
    }

    @Test
    fun test2() {
        val actual = try {
            "SUCCESS".toString()
            throw Exception()
        } catch (e: Exception) {
            "FAIL".toString()
        }
        assertEquals("FAIL", actual)
    }

    @Test
    fun test3() {
        val actual = try {
            throw Exception()
            "SUCCESS".toString()
        } catch (e: Exception) {
            "FAIL".toString()
        }
        assertEquals("FAIL", actual)
    }

    @Test
    fun test4() {
        val actual = try {
            "SUCCESS".toString()
        } catch (e: Exception) {
        }
        assertEquals("SUCCESS", actual)
    }

    @Test
    fun test5() {
        val actual = try {
            "SUCCESS".toString()
            throw Exception()
        } catch (e: Exception) {
        }
        assertEquals(Unit, actual)
    }

    @Test
    fun test6() {
        val actual = try {
            "SUCCESS".toString()
        } catch (e: Exception) {
            "FAIL".toString()
        } finally {
            "FINALLY".toString()
        }
        assertEquals("SUCCESS", actual)
    }

    @Test
    fun test7() {
        val actual = try {
            "SUCCESS".toString()
            throw Exception()
        } catch (e: Exception) {
            "FAIL".toString()
        } finally {
            "FINALLY".toString()
        }
        assertEquals("FAIL", actual)
    }
}