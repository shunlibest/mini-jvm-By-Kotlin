package com.gxk.jvm.classfile

import com.gxk.jvm.classfile.ClassReader.read
import com.gxk.jvm.classfile.ClassReader.readByteCode
import kotlin.Throws
import java.io.IOException
import java.io.FileInputStream
import com.gxk.jvm.classfile.ClassReaderTest
import org.junit.Assert
import org.junit.Test
import java.io.DataInputStream

class ClassReaderTest {
    //  @Test
    //  public void test_hello() throws IOException {
    //    String hello = "example/Hello.class";
    //    test(hello);
    //  }
    //
    //  @Test
    //  public void test_loop() throws IOException {
    //    String loop = "example/Loop1.class";
    //    test(loop);
    //  }
    //
    //  @Test
    //  public void test_int1() throws IOException {
    //    String int1 = "example/Int3Impl.class";
    //    test(int1);
    //  }
    //
    //  @Test
    //  public void test_exception() throws IOException {
    //    String clazz = "/Users/gxk/toy/java/mini-jvm/example/target/classes/Hello.class";
    //    test(clazz);
    //  }
    @Throws(IOException::class)
    fun test(p: String?) {
        val cf = read(DataInputStream(FileInputStream(p)))
        Assert.assertNotNull(cf)
    }

    @Test
    @Throws(IOException::class)
    fun testReadInst_main() {
//        val main = "033c043d1c100aa3000d1b1c603c840201a7fff3b1"
//        val bytes = toBytes(main)
//        val instructions = readByteCode(bytes, null!!)
//        Assert.assertEquals(14, instructions.size.toLong())
    }

    companion object {
        fun toBytes(str: String?): ByteArray {
            if (str == null || str.trim { it <= ' ' } == "") {
                return ByteArray(0)
            }
            val bytes = ByteArray(str.length / 2)
            for (i in 0 until str.length / 2) {
                val subStr = str.substring(i * 2, i * 2 + 2)
                bytes[i] = subStr.toInt(16).toByte()
            }
            return bytes
        }
    }
}