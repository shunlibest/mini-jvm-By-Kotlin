package com.gxk.jvm.classpath

import com.gxk.jvm.classfile.ClassFile
import com.gxk.jvm.classfile.ClassReader
import java.util.Objects
import com.gxk.jvm.util.EnvHolder
import java.io.IOException
import com.gxk.jvm.classpath.DirEntry
import java.io.File
import java.lang.IllegalArgumentException

class DirEntry(private val dirPath: String) : Entry {

    override fun findClass(clazzName: String): ClassFile? {
        if (!clazzName.contains("/")) {
            val list = File(dirPath).list() ?: throw IllegalArgumentException()
            for (name in list) {
                if (name == "$clazzName.class") {
                    val path = dirPath + EnvHolder.FILE_SEPARATOR + clazzName + ".class"
                    val cf: ClassFile = try {
                        ClassReader.read(path)
                    } catch (e: IOException) {
                        throw IllegalArgumentException()
                    }
                    cf.source = path
                    return cf
                }
            }
            return null
        }
        val idx = clazzName.indexOf("/")
        val subDir = clazzName.substring(0, idx)
        val subPath = dirPath + EnvHolder.FILE_SEPARATOR + subDir
        return if (!File(subPath).exists()) {
            null
        } else DirEntry(subPath).findClass(clazzName.substring(idx + 1))
    }
}