package com.gxk.jvm.nativebridge.java.lang

import com.gxk.jvm.interpret.Interpreter
import com.gxk.jvm.rtda.heap.Heap.registerNativeMethod
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.registerClass
import com.gxk.jvm.rtda.UnionSlot
import com.gxk.jvm.rtda.heap.*
import com.gxk.jvm.util.Utils
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

object ClassBridge {
    fun registerNatives0() {
        registerNativeMethod("java/lang/Class_registerNatives_()V") { frame: Frame? -> }
        registerNativeMethod("java/lang/Class_getName0_()Ljava/lang/String;") { frame: Frame ->
            val obj = frame.popRef()
            val name = obj.metaClass.name
            val strClazz = findClass("java/lang/String")
            val nameObj = strClazz!!.newInstance()
            val chars = Utils.replace(name, '/', '.').toCharArray()
            val array = PrimitiveArray.charArray(chars.size)
            for (i in chars.indices) {
                array.ints[i] = chars[i].toInt()
            }
            nameObj.setField("value", "[C", UnionSlot.of(array))
            frame.pushRef(nameObj)
        }
        registerNativeMethod(
                "java/lang/Class_forName0_(Ljava/lang/String;ZLjava/lang/ClassLoader;Ljava/lang/Class;)Ljava/lang/Class;"
        ) { frame: Frame ->
            frame.popRef()
            frame.popRef()
            val init = frame.popInt()
            val name = frame.popRef()
            val clsName = Utils.replace(Utils.obj2Str(name), '.', '/')
            var clazz = findClass(clsName)
            if (clazz == null) {
                clazz = frame.method.clazz.classLoader.loadClass(clsName)
            }
            checkNotNull(clazz) { "class not found $clsName" }
            frame.pushRef(clazz.runtimeClass)
            if (init == 1 && !(clazz.judgeStat())) {
                val cinit = clazz.clinitMethod ?: throw IllegalStateException()
                clazz.stat = 1
                Interpreter.execute(cinit)
                clazz.stat = 2
            }
        }
        registerNativeMethod("java/lang/Class_isInstance_(Ljava/lang/Object;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_isAssignableFrom_(Ljava/lang/Class;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_isInterface_()Z") { frame: Frame ->
            val cls = frame.popRef().metaClass
            frame.pushInt(if (cls.isInterface) 1 else 0)
        }
        registerNativeMethod("java/lang/Class_isArray_()Z") { frame: Frame ->
            val metaClass = frame.popRef().metaClass
            val isArray = metaClass.name.startsWith("[")
            frame.pushInt(if (isArray) 1 else 0)
        }
        registerNativeMethod("java/lang/Class_isPrimitive_()Z") { frame: Frame ->
            val cls = frame.popRef().metaClass
            val isPrimitive = cls.isPrimitive
            frame.pushInt(if (isPrimitive) 1 else 0)
        }
        registerNativeMethod("java/lang/Class_getSuperclass_()Ljava/lang/Class;") { frame: Frame ->
            val superClass = frame.popRef().metaClass.superClass
            if (superClass == null) {
                frame.pushRef(null)
                return@registerNativeMethod
            }
            frame.pushRef(superClass.runtimeClass)
        }
        registerNativeMethod("java/lang/Class_getInterfaces0_()[Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getComponentType_()Ljava/lang/Class;") { frame: Frame ->
            val cls = frame.popRef().metaClass
            if (cls.name.startsWith("[")) {
                val name = cls.name.substring(1)
                when (name) {
                    "C" -> {
                        val ccls = findClass("java/lang/Character")
                        val runtimeClass = ccls!!.runtimeClass
                        frame.pushRef(runtimeClass)
                    }
                    else -> throw UnsupportedOperationException()
                }
            }
        }
        registerNativeMethod("java/lang/Class_getModifiers_()I") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getSigners_()[Ljava/lang/Object;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_setSigners_([Ljava/lang/Object;)V") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getEnclosingMethod0_()[Ljava/lang/Object;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getDeclaringClass0_()Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getProtectionDomain0_()Ljava/security/ProtectionDomain;"
        ) { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getGenericSignature0_()Ljava/lang/String;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getRawAnnotations_()[B") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getRawTypeAnnotations_()[B") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getConstantPool_()Lsun/reflect/ConstantPool;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getDeclaredFields0_(Z)[Ljava/lang/reflect/Field;"
        ) { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getDeclaredMethods0_(Z)[Ljava/lang/reflect/Method;"
        ) { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod(
                "java/lang/Class_getDeclaredConstructors0_(Z)[Ljava/lang/reflect/Constructor;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getDeclaredClasses0_()[Ljava/lang/Class;") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_desiredAssertionStatus0_(Ljava/lang/Class;)Z") { frame: Frame? -> throw UnsupportedOperationException() }
        registerNativeMethod("java/lang/Class_getPrimitiveClass_(Ljava/lang/String;)Ljava/lang/Class;"
        ) { frame: Frame ->
            val instance = frame.popRef()
            val `val` = Utils.obj2Str(instance)
            val cls = findClass(`val`)
            frame.pushRef(cls!!.runtimeClass)
        }
        registerNativeMethod("java/lang/Class_desiredAssertionStatus_()Z") { frame: Frame ->
            val xx: Any = frame.popRef()
            frame.pushInt(1)
        }

        // hack
        registerNativeMethod("java/lang/Class_getSimpleName_()Ljava/lang/String;") { frame: Frame ->
            val cls = frame.popRef().metaClass
            val lidx = cls.name.lastIndexOf("/")
            var idx = 0
            if (lidx > 0) {
                idx = lidx + 1
            }
            val sn = cls.name.substring(idx)
            val obj = Utils.str2Obj(sn, frame.method.clazz.classLoader)
            frame.pushRef(obj)
        }
        registerNativeMethod("java/lang/Class_getCanonicalName_()Ljava/lang/String;") { frame: Frame ->
            val cls = frame.popRef().metaClass
            val sn = Utils.replace(cls.name, '/', '.')
            val obj = Utils.str2Obj(sn, frame.method.clazz.classLoader)
            frame.pushRef(obj)
        }
        registerNativeMethod("java/lang/Class_getInterfaces_()[Ljava/lang/Class;") { frame: Frame ->
            val thisObj = frame.popRef()
            val cls = thisObj.metaClass
            if (!cls.interfaceNames.isEmpty() && cls.interfaces.isEmpty()) {
                frame.pushRef(thisObj)
                cls.interfaceInit(frame)
                return@registerNativeMethod
            }
            val interfaces = cls.interfaces
            val count = interfaces.size
            val name = "[Ljava/lang/Class;"
            var clazz = findClass(name)
            if (clazz == null) {
                clazz = Class(1, name, frame.method.clazz.classLoader, null)
                clazz!!.superClass = findClass("java/lang/Object")
                clazz!!.stat = 2
                registerClass(name, clazz!!)
            }
            val objs = arrayOfNulls<Instance>(count)
            for (i in interfaces.indices) {
                objs[i] = interfaces[i].runtimeClass
            }
            val instanceArray = InstanceArray(clazz, objs)
            frame.pushRef(instanceArray)
        }
        registerNativeMethod("java/lang/Class_newInstance_()Ljava/lang/Object;") { frame: Frame ->
            val cls = frame.popRef().metaClass
            val obj = cls.newInstance()
            frame.pushRef(obj)
        }
        registerNativeMethod(
                "java/lang/Class_getDeclaredField_(Ljava/lang/String;)Ljava/lang/reflect/Field;") { frame: Frame ->
            val nameObj = frame.popRef()
            val thisObj = frame.popRef()
            val name = Utils.obj2Str(nameObj)
            val field = thisObj.metaClass.getField(name)
            frame.pushRef(null)
        }
    }
}