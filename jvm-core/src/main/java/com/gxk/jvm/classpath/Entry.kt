package com.gxk.jvm.classpath

import com.gxk.jvm.classfile.ClassFile


public interface Entry {
    fun findClass(name: String): ClassFile?
}