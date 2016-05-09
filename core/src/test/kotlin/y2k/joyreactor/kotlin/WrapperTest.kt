package y2k.joyreactor.kotlin

import org.junit.Test

/**
 * Created by y2k on 5/9/16.
 */
class WrapperTest {

    @Test
    fun test() {

        val base1 = object : BaseInterface {
            override fun method1(arg: Int, arg2: Int) {
                println("BASE 1 :: method 1")
            }

            override fun method2(arg: String) {
                println("BASE 1 :: method 2")
            }
        }
        val base2 = object : BaseInterface {
            override fun method1(arg: Int, arg2: Int) {
                println("BASE 2 :: method 1")
            }

            override fun method2(arg: String) {
                println("BASE 2 :: method 2")
            }
        }


        val a = WrapperClass(base1)
        a.method1(0, 0)

        a.base = base2
        a.method1(0, 0)
        a.method2("")
    }

    class WrapperClass(var base: BaseInterface) : BaseInterface by base {
    }

    interface BaseInterface {
        fun method1(arg: Int, arg2: Int);
        fun method2(arg: String);
    }
}