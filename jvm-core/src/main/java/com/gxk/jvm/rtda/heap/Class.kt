package com.gxk.jvm.rtda.heap

import com.gxk.jvm.classfile.ClassFile
import com.gxk.jvm.classfile.ConstantPool
import com.gxk.jvm.classfile.attribute.BootstrapMethods
import com.gxk.jvm.classloader.ClassLoader
import com.gxk.jvm.interpret.Interpreter
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.findClass
import java.util.function.Consumer

class Class {
    val accessFlags: Int
    val name: String
    val superClassName: String?
    val interfaceNames: List<String>
    val methods: List<Method>
    val fields: List<Field>
    val bootstrapMethods: BootstrapMethods?
    val constantPool: ConstantPool?
    val classLoader: ClassLoader
    val classFile: ClassFile?
    var superClass: Class? = null
    var interfaces: List<Class>
    var stat = 0
    var runtimeClass: Instance? = null

    constructor(accessFlags: Int, name: String, classLoader: ClassLoader, classFile: ClassFile?) {
        this.accessFlags = accessFlags
        this.name = name
        this.classFile = classFile
        superClassName = "java/lang/Object"
        interfaceNames = ArrayList()
        interfaces = ArrayList()
        bootstrapMethods = null
        constantPool = null
        this.classLoader = classLoader
        methods = ArrayList()
        fields = ArrayList()
        stat = 2
    }

    constructor(accessFlags: Int, name: String, classLoader: ClassLoader) {
        this.accessFlags = accessFlags
        this.name = name
        classFile = null
        superClassName = null
        interfaceNames = ArrayList()
        interfaces = ArrayList()
        bootstrapMethods = null
        constantPool = null
        this.classLoader = classLoader
        methods = ArrayList()
        fields = ArrayList()
        stat = 2
    }

    constructor(
            accessFlags: Int,
            name: String,
            superClassName: String?,
            interfaceNames: List<String>,
            methods: List<Method>,
            fields: List<Field>,
            bootstrapMethods: BootstrapMethods?,
            constantPool: ConstantPool?,
            classLoader: ClassLoader,
            classFile: ClassFile?) {
        this.accessFlags = accessFlags
        this.name = name
        this.superClassName = superClassName
        this.interfaceNames = interfaceNames
        this.classFile = classFile
        interfaces = ArrayList()
        this.methods = methods
        this.fields = fields
        this.bootstrapMethods = bootstrapMethods
        this.constantPool = constantPool
        this.classLoader = classLoader
        methods.forEach(Consumer { it: Method -> it.clazz = this })
    }


    val clinitMethod: Method?
        get() = getMethod("<clinit>", "()V")

    /**
     * 获取类中的main方法
     * @return Method?
     */
    fun getMethod(methodName: String): Method? {
        return methods.find {
            it.name == methodName
        }
    }

    fun getMethod(name: String, descriptor: String?): Method {
        val methodCheck = getMethodCheck(name, descriptor)

        if (methodCheck == null) {
            throw Exception("找不到方法")
        }
        return methodCheck
    }

    private fun getMethodCheck(name: String, descriptor: String?): Method? {
        for (method in methods) {
            if (method.name == name && method.descriptor == descriptor) {
                return method
            }
        }
        for (inter in interfaces) {
            val method = inter.getMethodCheck(name, descriptor)
            if (method != null) {
                return method
            }
        }
        return if (superClass == null) {
            null
        } else superClass!!.getMethodCheck(name, descriptor)
    }

    fun getField(fieldName: String?, fieldDescriptor: String?): Field {
        return getFieldCheck(fieldName, fieldDescriptor) ?: throw Exception("getField fail")
    }

    private fun getFieldCheck(fieldName: String?, fieldDescriptor: String?): Field? {
        for (field in fields) {
            if (field.name == fieldName && field.descriptor == fieldDescriptor) {
                return field
            }
        }
        return null
    }

    fun getField(fieldName: String?): Field? {
        for (field in fields) {
            if (field.name == fieldName) {
                return field
            }
        }
        return null
    }

    fun newInstance(): Instance {
        val newFields: MutableList<Field> = ArrayList()
        for (field in fields) {
            newFields.add(this.map(field))
        }
        val `object` = Instance(newFields, this)
        if (superClass != null) {
            `object`.setSuperInstance(superClass!!.newInstance())
        }
        return `object`
    }

    fun newLambdaObject(args: List<Any?>?): LambdaObject {
        return LambdaObject(this, args)
    }

    private fun map(source: Field): Field {
        if (source.isStatic) {
            return source
        }
        val field = Field(source.accessFlags, source.name, source.descriptor)
        field.init()
        return field
    }

    fun judgeStat(): Boolean {
        return stat > 0
    }


    val unStaticInitSuperClass: Class?
        get() {
            if (!judgeStat()) {
                return this
            }
            return if (superClass == null) {
                null
            } else superClass!!.unStaticInitSuperClass
        }

    override fun toString(): String {
        return "KClass{" +
                "name='" + name + '\'' +
                ", superClassName='" + superClassName + '\'' +
                ", methods=" + methods.size +
                ", fields=" + fields.size +
                ", classLoader=" + classLoader.javaClass.name +
                ", superClass=" + (if (superClass == null) "null" else superClass!!.name) +
                ", staticInit=" + stat +
                '}'
    }

    fun interfaceInit(frame: Frame) {
        val interfaces: MutableList<Class> = ArrayList()
        for (interfaceName in interfaceNames) {
            var tmp = findClass(interfaceName)
            if (tmp == null) {
                tmp = frame.method.clazz.classLoader.loadClass(interfaceName)
            }
            tmp.interfaceInit(frame)
            interfaces.add(tmp)
            if (!tmp.judgeStat()) {
                val cinit = tmp.clinitMethod ?: throw IllegalStateException()
                tmp.stat = 1
                Interpreter.execute(cinit)
                tmp.stat = 2
            }
        }
        this.interfaces = interfaces
    }

    fun `is`(clazz: String): Boolean {
        if (name == clazz) {
            return true
        }
        for (interfaceName in interfaceNames) {
            if (interfaceName == clazz) {
                return true
            }
        }
        return if (superClass != null) {
            superClass!!.`is`(clazz)
        } else false
    }

    val isInterface: Boolean
        get() = accessFlags and 0x0200 != 0
    val isPrimitive: Boolean
        get() {
            if (name == "java/lang/Character") {
                return true
            }
            println("is primitive ? $name")
            return false
        }

    fun getSource(): String? {
        return classFile!!.sourceFile
    }
}