package com.gxk.jvm.classfile

import kotlin.Throws
import java.io.IOException
import java.io.FileInputStream
import java.lang.UnsupportedOperationException
import com.gxk.jvm.classfile.attribute.BootstrapMethods.BootstrapMethod
import com.gxk.jvm.classfile.attribute.BootstrapMethods
import com.gxk.jvm.classfile.attribute.CodeAttribute
import com.gxk.jvm.classfile.attribute.LineNumberTable
import com.gxk.jvm.classfile.attribute.SourceFile
import com.gxk.jvm.classfile.cp.*
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.util.Utils
import java.io.DataInputStream
import java.util.ArrayList

/**
 * 读取.class文件信息
 *
 */
object ClassReader {
    @Throws(IOException::class)
    fun read(path: String): ClassFile {
        FileInputStream(path).use { inputStream ->
            object : DataInputStream(inputStream) {}.use { stm -> return read(stm) }
        }
    }

    @Throws(IOException::class)
    fun read(inputStream: DataInputStream): ClassFile {
        val magic = inputStream.readInt()
        val minorVersion = inputStream.readUnsignedShort()
        val majorVersion = inputStream.readUnsignedShort()
        val cpSize = inputStream.readUnsignedShort()
        val constantPool = readConstantPool(inputStream, cpSize - 1)
        val accessFlag = inputStream.readUnsignedShort()
        val thisClass = inputStream.readUnsignedShort()
        val superClass = inputStream.readUnsignedShort()
        val interfaceCount = inputStream.readUnsignedShort()
        val interfaces = readInterfaces(inputStream, interfaceCount, constantPool)
        val fieldCount = inputStream.readUnsignedShort()
        val fields = readFields(inputStream, fieldCount, constantPool)
        val methodCount = inputStream.readUnsignedShort()
        val methods = readMethods(inputStream, methodCount, constantPool)
        val attributeCount = inputStream.readUnsignedShort()
        val attributes = readAttributes(inputStream, attributeCount, constantPool)
        return ClassFile(
                magic,
                minorVersion,
                majorVersion,
                cpSize,
                constantPool,
                accessFlag,
                thisClass,
                superClass,
                interfaceCount,
                interfaces,
                fieldCount,
                fields,
                methodCount,
                methods,
                attributeCount,
                attributes
        )
    }

    @Throws(IOException::class)
    private fun readInterfaces(input: DataInputStream, interfaceCount: Int, cp: ConstantPool): Interfaces {
        val interfaces = arrayOfNulls<Interface>(interfaceCount)
        for (i in 0 until interfaceCount) {
            val idx = input.readUnsignedShort()
            val name = Utils.getClassName(cp, idx)
            interfaces[i] = Interface(name)
        }
        return Interfaces(interfaces)
    }

    @Throws(IOException::class)
    private fun readFields(input: DataInputStream, fieldCount: Int, constantPool: ConstantPool): Fields {
        val fieldInfos = arrayOfNulls<FieldInfo>(fieldCount)
        for (i in 0 until fieldCount) {
            val accessFlag = input.readUnsignedShort()
            val nameIndex = input.readUnsignedShort()
            val descriptorIndex = input.readUnsignedShort()
            val attributesCount = input.readUnsignedShort()
            val attributes = readAttributes(input, attributesCount, constantPool)
            val name = (constantPool.infos[nameIndex - 1] as Utf8).string
            val descriptor = (constantPool.infos[descriptorIndex - 1] as Utf8).string
            val fieldInfo = FieldInfo(accessFlag, name, Descriptor(descriptor), attributes)
            fieldInfos[i] = fieldInfo
        }
        return Fields(fieldInfos)
    }


    @Throws(IOException::class)
    private fun readMethods(input: DataInputStream, methodCount: Int,
                            constantPool: ConstantPool): Methods {
        val methods = Methods(methodCount)
        for (i in 0 until methodCount) {
            val accessFlag = input.readUnsignedShort()
            val nameIndex = input.readUnsignedShort()
            val descriptorIndex = input.readUnsignedShort()
            val attributesCount = input.readUnsignedShort()
            val attributes = readAttributes(input, attributesCount, constantPool)
            val info = constantPool.infos[nameIndex - 1]
            val name = (info as Utf8).string
            val descriptor = (constantPool.infos[descriptorIndex - 1] as Utf8).string
            methods.methodInfos[i] = MethodInfo(accessFlag, name, Descriptor(descriptor), attributes)
        }
        return methods
    }

    @Throws(IOException::class)
    private fun readConstantPool(input: DataInputStream, cpSize: Int): ConstantPool {
        val constantPool = ConstantPool(cpSize)
        var i = 0
        while (i < cpSize) {
            val tag = input.readUnsignedByte()
            val info: ConstantInfo = when (tag) {
                ConstantPoolInfoEnum.CONSTANT_Utf8 -> {
                    val length = input.readUnsignedShort()
                    val bytes = Utils.readNBytes(input, length)
                    Utf8(tag, bytes)
                }
                //3 - 6
                ConstantPoolInfoEnum.CONSTANT_Integer -> IntegerCp(tag, input.readInt())
                ConstantPoolInfoEnum.CONSTANT_Float -> FloatCp(tag, input.readFloat())
                ConstantPoolInfoEnum.CONSTANT_Long -> LongCp(tag, input.readLong())
                ConstantPoolInfoEnum.CONSTANT_Double -> DoubleCp(tag, input.readDouble())

                //7:类名称字符串索引: 个数=1(本身类名)+1(继承,包括Object类)+接口数量
                ConstantPoolInfoEnum.CONSTANT_Class -> ClassCp(tag, input.readUnsignedShort())
                //8:对应Java中的String, 有可能不存在, 指向utf8
                ConstantPoolInfoEnum.CONSTANT_String -> StringCp(tag, input.readUnsignedShort())

                //9:字段常量: 拼凑com/shunli/HelloWorld + x:I
                ConstantPoolInfoEnum.CONSTANT_Fieldref -> {
                    FieldDef(tag, input.readUnsignedShort(), input.readUnsignedShort())
                }
                //10:方法执行 java/io/PrintStream + println:(Ljava/lang/String;)V
                ConstantPoolInfoEnum.CONSTANT_Methodref -> {
                    MethodDef(tag, input.readUnsignedShort(), input.readUnsignedShort())
                }
                ConstantPoolInfoEnum.CONSTANT_InterfaceMethodref -> {
                    InterfaceMethodDef(tag, input.readUnsignedShort(), input.readUnsignedShort())
                }
                //12: 名称和类型常量:字段和方法通过描述符表达了字段和方法的类型 + 名称（Name)
                // 包括变量 和 执行的方法
                //注:"<init>":()V 原因
                ConstantPoolInfoEnum.CONSTANT_NameAndType -> {
                    NameAndType(tag, input.readUnsignedShort(), input.readUnsignedShort())
                }

                ConstantPoolInfoEnum.CONSTANT_MethodHandle -> {
                    MethodHandle(tag, input.readUnsignedByte(), input.readUnsignedShort())
                }
                ConstantPoolInfoEnum.CONSTANT_MethodType -> {
                    MethodType(tag, input.readUnsignedShort())
                }
                ConstantPoolInfoEnum.CONSTANT_InvokeDynamic -> {
                    InvokeDynamic(tag, input.readUnsignedShort(), input.readUnsignedShort())
                }
                else -> throw UnsupportedOperationException("un parse cp $tag")
            }
            constantPool.infos[i] = info
            if (info.infoEnum == ConstantPoolInfoEnum.CONSTANT_Double
                    || info.infoEnum == ConstantPoolInfoEnum.CONSTANT_Long) {
                i++
            }
            i++
        }
        return constantPool
    }

    //  public static String byteArrayToHex(byte[] a) {
    //    StringBuilder sb = new StringBuilder(a.length * 2);
    //    for (byte b : a) {
    //      sb.append(String.format("%02x", b));
    //    }
    //    return sb.toString();
    //  }
    //  attribute_info {
    //    u2 attribute_name_index;
    //    u4 attribute_length;
    //    u1 info[attribute_length];
    //  }
    //属性表
    @Throws(IOException::class)
    private fun readAttributes(input: DataInputStream, attributeCount: Int,
                               constantPool: ConstantPool): Attributes {
        val attributes = Attributes(attributeCount)
        for (i in 0 until attributeCount) {
            var attribute: Attribute? = null
            val attributeNameIndex = input.readUnsignedShort()
            val attributeName = Utils.getString(constantPool, attributeNameIndex)
            val attributeLength = input.readInt()
            when (attributeName) {
                AttributeEnum.SourceFile -> {
                    val sourceFileIndex = input.readUnsignedShort()
                    val file = Utils.getString(constantPool, sourceFileIndex)
                    attribute = SourceFile(file)
                }
                //方法所需的属性
                AttributeEnum.Code -> {
                    val maxStack = input.readUnsignedShort()
                    val maxLocals = input.readUnsignedShort()
                    val codeLength = input.readInt()        //code[]数组的字节数
                    val byteCode = Utils.readNBytes(input, codeLength)
                    val instructions = readByteCode(byteCode, constantPool)
                    val exceptionTable = readExceptionTable(input, constantPool)
                    val codeAttributeCount = input.readUnsignedShort()
                    val codeAttributes = readAttributes(input, codeAttributeCount, constantPool)//杂项属性
                    attribute = CodeAttribute(maxStack, maxLocals, instructions, exceptionTable, codeAttributes)
                }
                //行号和字节码号对应表, 位于code中
                AttributeEnum.LineNumberTable -> {
                    val length = input.readUnsignedShort()
                    val lines = arrayOfNulls<LineNumberTable.Line>(length)
                    for (i1 in 0..length) {
                        lines[i1] = LineNumberTable.Line(input.readUnsignedShort(), input.readUnsignedShort())
                    }
                    attribute = LineNumberTable(lines)
                }
                AttributeEnum.BootstrapMethods -> {
                    val bsmLen = input.readUnsignedShort()
                    val bootstrapMethods = arrayOfNulls<BootstrapMethod>(bsmLen)
                    var i1 = 0
                    while (i1 < bsmLen) {
                        val bsmr = input.readUnsignedShort()
                        val nbma = input.readUnsignedShort()
                        val args = arrayOfNulls<Int>(nbma)
                        var i2 = 0
                        while (i2 < nbma) {
                            args[i2] = input.readUnsignedShort()
                            i2++
                        }
                        bootstrapMethods[i1] = BootstrapMethod(bsmr, args)
                        i1++
                    }
                    attribute = BootstrapMethods(bootstrapMethods)
                }
                else ->{
                    //TODO 日后解析
                    Utils.readNBytes(input, attributeLength)
                }
            }
            attributes.attributes[i] = attribute
        }
        return attributes
    }

    //读取方法code的内容
    @Throws(IOException::class)
    fun readByteCode(byteCode: ByteArray, constantPool: ConstantPool): Array<Instruction> {
        val instructions: MutableList<Instruction> = ArrayList()
        MyDataInputStream(MyByteArrayInputStream(byteCode)).use { stm ->
            while (stm.available() > 0) {
                val opCode = stm.readUnsignedByte()
                val inst = InstructionReader.read(opCode, stm, constantPool)
                instructions.add(inst)
            }
        }
        return instructions.toTypedArray()
    }

    //读取异常表
    private fun readExceptionTable(input: DataInputStream, constantPool: ConstantPool): ExceptionTable {
        val exceptionTableLength = input.readUnsignedShort()    //异常表的长度
        val exceptions = arrayOfNulls<Exception>(exceptionTableLength)
        var i1 = 0
        while (i1 < exceptionTableLength) {
            val etsp = input.readUnsignedShort()
            val etep = input.readUnsignedShort()
            val ethp = input.readUnsignedShort()
            val ctIdx = input.readUnsignedShort()

            // null => catch any exception
            var etClassname: String? = null
            if (ctIdx != 0) {
                etClassname = Utils.getClassName(constantPool, ctIdx)
            }
            val exception = Exception(etsp, etep, ethp, etClassname)
            exceptions[i1] = exception
            i1++
        }
        return ExceptionTable(exceptions)
    }
}