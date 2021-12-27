package com.gxk.jvm.classloader

import com.gxk.jvm.VirtualMachine.Companion.loadLibrary
import com.gxk.jvm.rtda.heap.Heap.clear
import java.nio.file.Paths
import com.gxk.jvm.util.EnvHolder
import java.lang.IllegalStateException
import com.gxk.jvm.classpath.Classpath
import com.gxk.jvm.rtda.heap.Heap
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ClassLoaderTest {
    private var classLoader: ClassLoader? = null
    @Before
    fun setup() {
        val home = System.getenv("JAVA_HOME")
        val jarPath = Paths.get(home, "jre", "lib", "rt.jar")

        // check MINI_JVM_HOME ready
        // 1. env
        var miniJvmHome = System.getenv("MINI_JVM_HOME")
        if (miniJvmHome == null) {
            // 1.2 check current dir
            val userDir = System.getProperty("user.dir")
            if (userDir.endsWith("jvm-core")) {
                val idx = userDir.lastIndexOf(EnvHolder.FILE_SEPARATOR)
                miniJvmHome = userDir.substring(0, idx)
            } else if (userDir.endsWith("mini-jvm")) {
                miniJvmHome = userDir
            }
        }
        checkNotNull(miniJvmHome) { "MINI_JVM_HOME not found" }
        val exampleJarPath = Paths.get(miniJvmHome, "example", "target", "example.jar")
        check(exampleJarPath.toFile().exists()) { "example.jar not found" }
        val entry = Classpath.parse(exampleJarPath.toFile().absolutePath + EnvHolder.PATH_SEPARATOR + jarPath.toFile().absolutePath)
        classLoader = ClassLoader("boot", entry)
        loadLibrary()
    }

    @After
    fun tearDown() {
        clear()
    }

    @Test
    fun test_object() {
        val aClass = classLoader!!.loadClass("java/lang/Object")
        Assert.assertNotNull(aClass)
    }

    @Test
    fun test_hello() {
        val aClass = classLoader!!.loadClass("Hello")
        Assert.assertNotNull(aClass)
    }

    @Test
    fun test_onjava8_passobject() {
        val clazz = classLoader!!.loadClass("PassObject")
        Assert.assertNotNull(clazz)
    }

    @Test
    fun test_system() {
        val aClass = classLoader!!.loadClass("java/lang/System")
        Assert.assertNotNull(aClass)
    }
}