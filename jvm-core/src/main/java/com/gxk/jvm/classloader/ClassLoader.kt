package com.gxk.jvm.classloader

import com.gxk.jvm.classfile.ClassFile
import com.gxk.jvm.classfile.FieldInfo
import com.gxk.jvm.classfile.MethodInfo
import com.gxk.jvm.classfile.attribute.BootstrapMethods
import com.gxk.jvm.classpath.Entry
import com.gxk.jvm.rtda.heap.*
import com.gxk.jvm.util.Utils
import java.util.ArrayList

/**
 * 类加载器
 */
public class ClassLoader(val name: String, private val entry: Entry) {
    fun loadPrimitiveClass(name: String) {
        val cache = Heap.findClass(name)
        if (cache != null) {
            return
        }
        val cls = Class(1, name, this)
        val metaCls = Heap.findClass("java/lang/Class")!!.newInstance()
        cls.runtimeClass = metaCls
        metaCls.metaClass = cls
        doRegister(cls)
    }

    fun loadPrimitiveArrayClass(name: String) {
        val cache = Heap.findClass(name)
        if (cache != null) {
            return
        }
        val cls = Class(1, name, this)
        val metaCls = Heap.findClass("java/lang/Class")!!.newInstance()
        cls.runtimeClass = metaCls
        metaCls.metaClass = cls
        doRegister(cls)
    }

    /**
     * 加载某一个类
     * @param name 要加载类的类名
     * @return Class 信息
     */
    fun loadClass(name: String): Class {
        //先从堆中获取cache数据
        val cache = Heap.findClass(name)
        if (cache != null) {
            return cache
        }
        val clazz = doLoadClass(name)
        doRegister(clazz)
        return clazz
    }

    fun doRegister(clazz: Class) {
        Heap.registerClass(clazz.name, clazz)
        for (method in clazz.methods) {
            if (method.isNative) {
                val key = Utils.genNativeMethodKey(method.clazz.name, method.name, method.descriptor)
                val nm = Heap.findMethod(key)
                if (nm == null) {
                    System.err.println("not found native method $key $method")
                }
            }
        }
    }

    private fun doLoadClass(name: String): Class {
        val clazz: ClassFile? = entry.findClass(name)
        if (clazz == null) {
            throw Exception("未找到class:$name")
        }

        val aClass = doLoadClass(name, clazz)

        // superclass
        if (aClass.superClassName != null) {
            aClass.superClass = loadClass(aClass.superClassName)
        }
        val langClass = Heap.findClass("java/lang/Class");
        if (langClass != null) {
            val rcs = langClass.newInstance()
            aClass.runtimeClass = rcs
            rcs.metaClass = aClass
        }
        return aClass
    }

    private fun doLoadClass(name: String, classFile: ClassFile): Class {
        val methods: MutableList<Method> = ArrayList()
        for (methodInfo in classFile.methods.methodInfos) {
            methods.add(this.map(methodInfo))
        }
        val fields: MutableList<Field> = ArrayList()
        for (fieldInfo in classFile.fields.fieldInfos) {
            fields.add(this.map(fieldInfo))
        }

        // field interfaceInit
        for (it in fields) {
            if (it.isStatic) {
                it.init()
            }
        }
        val scIdx = classFile.superClass
        var superClassName: String? = null
        if (scIdx != 0) {
            superClassName = Utils.getClassName(classFile.cpInfo, scIdx)
        }
        val interfaceNames: MutableList<String> = ArrayList()
        if (classFile.interfaces.interfaces.size != 0) {
            for (anInterface in classFile.interfaces.interfaces) {
                interfaceNames.add(anInterface.getName())
            }
        }
        val bootstrapMethods = classFile.bootstrapMethods
        return Class(classFile.accessFlags, name, superClassName, interfaceNames, methods, fields,
                bootstrapMethods, classFile.cpInfo, this, classFile)
    }

    fun map(cfMethodInfo: MethodInfo): Method {
        val code = cfMethodInfo.code
                ?: return Method(cfMethodInfo.accessFlags, cfMethodInfo.name, cfMethodInfo.descriptor.descriptor, 0, 0,
                        null!!, null!!, cfMethodInfo.lineNumber)
        return Method(cfMethodInfo.accessFlags, cfMethodInfo.name, cfMethodInfo.descriptor.descriptor,
                code.maxStacks, code.maxLocals, code.getInstructions(), code.exceptionTable,
                cfMethodInfo.lineNumber)
    }

    fun map(fieldInfo: FieldInfo): Field {
        return Field(fieldInfo.accessFlags, fieldInfo.name, fieldInfo.descriptor.descriptor)
    }
}