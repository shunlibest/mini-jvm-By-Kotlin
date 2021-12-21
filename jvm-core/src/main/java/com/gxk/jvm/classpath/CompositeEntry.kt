package com.gxk.jvm.classpath

import com.gxk.jvm.classfile.ClassFile

class CompositeEntry(private val entries: List<Entry>) : Entry {

    override fun findClass(name: String): ClassFile? {
        for (entry in entries) {
            val cf = entry.findClass(name)
            if (cf != null) {
                return cf
            }
        }
        return null
    }
}