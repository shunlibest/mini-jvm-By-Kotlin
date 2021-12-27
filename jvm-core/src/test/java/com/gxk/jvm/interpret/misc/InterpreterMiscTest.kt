package com.gxk.jvm.interpret.misc

import com.gxk.jvm.interpret.Interpreter.runMain
import com.gxk.jvm.interpret.BaseInterpreterTest
import org.junit.Test

class InterpreterMiscTest : BaseInterpreterTest() {
    @Test
    fun test_hello_main() {
        testMain("Hello")
    }

    @Test
    fun test_hello_sb() {
        testMain("Hello2")
    }

    @Test
    fun test_hello_sb2() {
        testMain("Hello3")
    }

    @Test
    fun test_hello_long() {
        testMain("Hello4")
    }

    @Test
    fun test_static_field() {
        testMain("TestStatic")
    }

    @Test
    fun test_array_0() {
        val method = loadAndGetMainMethod("HelloWorld")
        runMain(method, arrayOf("hello", "mini-jvm"))
    }

    @Test
    fun test_array_1() {
        val method = loadAndGetMainMethod("HelloWorld")
        runMain(method, arrayOfNulls(0))
    }

    @Test
    fun test_switch_0() {
        testMain("SwitchTest0")
    }

    @Test
    fun test_switch_1() {
        testMain("SwitchTest1")
    }
}