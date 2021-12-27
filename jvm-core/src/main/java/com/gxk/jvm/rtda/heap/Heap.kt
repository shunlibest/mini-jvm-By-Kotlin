package com.gxk.jvm.rtda.heap

import java.lang.IllegalStateException
import com.gxk.jvm.util.EnvHolder
import com.gxk.jvm.util.Logger
import java.util.ArrayList
import java.util.HashMap

/**
 * jvm heap
 */
object Heap {
    //native方法
    private val NATIVE_METHOD_MAP: MutableMap<String, NativeMethod> = HashMap()

    //存储加载后的class方法
    private val STRING_K_CLASS_MAP: MutableMap<String, Class> = HashMap()

    /**
     * 注册本地方法
     *
     * @param key 方法名称, 包括类名
     * @param method 执行方法
     */
    fun registerNativeMethod(key: String, method: NativeMethod) {
        check(!NATIVE_METHOD_MAP.containsKey(key))
        NATIVE_METHOD_MAP[key] = method
    }

    fun findMethod(key: String): NativeMethod {
        return findMethodCheck(key) ?: throw IllegalStateException("do not findMethod:$key")
    }

    private fun findMethodCheck(key: String): NativeMethod? {
        return NATIVE_METHOD_MAP[key]
    }

    fun findClass(name: String): Class? {
        return STRING_K_CLASS_MAP[name]
    }

    fun registerClass(name: String, clazz: Class) {
        if (EnvHolder.verboseClass) {
            var source = clazz.classLoader.name
            if (clazz.classFile?.source != null) {
                source = clazz.classFile.source!!
            }
            Logger.clazz("[Loaded $name from $source]")
        }
        STRING_K_CLASS_MAP.putIfAbsent(name, clazz)
    }


    fun getClasses(): List<Class> {
        return ArrayList(STRING_K_CLASS_MAP.values)
    }

    // for test
    fun clear() {
        NATIVE_METHOD_MAP.clear()
        STRING_K_CLASS_MAP.clear()
    }

}