package com.gxk.jvm.classpath

import com.gxk.jvm.classfile.ClassFile
import com.gxk.jvm.classfile.ClassReader
import java.io.DataInputStream
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class JarEntry(val path: String) : Entry {

    override fun findClass(name: String): ClassFile? {
        val file: ZipFile = try {
            ZipFile(path)
        } catch (e: IOException) {
            throw IllegalStateException()
        }

        val entry = file.getEntry("$name.class") ?: return null
        try {
            file.getInputStream(entry).use { `is` ->
                val cf = ClassReader.read(DataInputStream(`is`))
                cf.source = path
                return cf
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException()
        }
    }
}