package com.gxk.jvm.classfile

import kotlin.Throws
import java.io.IOException
import com.gxk.jvm.instruction.constants.*
import com.gxk.jvm.classfile.ConstantInfo
import com.gxk.jvm.classfile.ConstantPoolInfoEnum
import com.gxk.jvm.classfile.cp.*
import com.gxk.jvm.instruction.constants.LdcInst
import java.lang.IllegalStateException
import com.gxk.jvm.instruction.constants.LdcWInst
import com.gxk.jvm.instruction.constants.Ldc2wInst
import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.instruction.comparisons.*
import com.gxk.jvm.instruction.loads.*
import com.gxk.jvm.instruction.stores.*
import com.gxk.jvm.instruction.stack.*
import com.gxk.jvm.instruction.math.*
import com.gxk.jvm.instruction.conversions.*
import com.gxk.jvm.instruction.control.*
import com.gxk.jvm.instruction.extended.GotoInst
import java.lang.UnsupportedOperationException
import java.util.LinkedHashMap
import com.gxk.jvm.instruction.references.*
import com.gxk.jvm.instruction.extended.*
import com.gxk.jvm.util.Utils

/**
 * 参考资料:
 * JVM指令手册 https://www.jianshu.com/p/0978d7ab7113
 */
object InstructionReader {

    fun read(opCode: Int, stm: MyDataInputStream, constantPool: ConstantPool): Instruction {
        return when (opCode) {
            //一、未归类系列A
            0x0 -> NopInst()        //空指令
            0x1 -> AconstNullInst() //将null推送至栈顶
            //二、const系列
            //该系列命令主要负责把简单的数值类型送到栈顶。该系列命令不带参数。注意只把简单的数值类型送到栈顶时,才使用如下的命令。
            0x2 -> IConstM1Inst()   //将int型(-1)推送至栈顶
            0x3 -> IConst0Inst()    //将int型(0)推送至栈顶
            0x4 -> IConst1Inst()    //将int型(1)推送至栈顶
            0x5 -> IConst2Inst()    //将int型(2)推送至栈顶
            0x6 -> IConst3Inst()    //将int型(3)推送至栈顶
            0x7 -> IConst4Inst()    //将int型(4)推送至栈顶
            0x8 -> IConst5Inst()    //将int型(5)推送至栈顶
            0x9 -> Lconst0Inst()    //将long型(0)推送至栈顶
            0xa -> Lconst1Inst()    //将long型(1)推送至栈顶
            0xb -> FConst0Inst()    //将float型(0)推送至栈顶
            0xc -> FConst1Inst()    //将float型(1)推送至栈顶
            0xd -> FConst2Inst()    //将float型(2)推送至栈顶
            0xe -> DConst0Inst()    //将double型(0)推送至栈顶
            0xf -> DConst1Inst()    //将double型(1)推送至栈顶
            //三、push系列
            //该系列命令负责把一个整形数字（长度比较小）送到到栈顶。该系列命令有一个参数，用于指定要送到栈顶的数字。
            //注意该系列命令只能操作一定范围内的整形数值，超出该范围的使用将使用ldc
            0x10 -> BiPushInst(stm.readByte())  //将单字节的常量值(-128~127)推送至栈顶
            0x11 -> SiPushInst(stm.readShort()) //将一个短整型常量值(-32768~32767)推送至栈顶

            //四、ldc系列
            //该系列命令负责把数值常量或String常量值从常量池中推送至栈顶。该命令后面需要给一个表示常量在常量池中位置(编号)的参数
            //注意: 通过new创建的String都是不放在常量池中
            0x12 -> {           //将int, float或String型常量值从常量池中推送至栈顶
                val index = stm.readUnsignedByte()
                val info = constantPool.infos[index - 1]
                when (info.infoEnum) {
                    ConstantPoolInfoEnum.CONSTANT_String -> {
                        val stringIndex = (info as StringCp).stringIndex
                        val string = Utils.getString(constantPool, stringIndex)
                        LdcInst("Ljava/lang/String", string)
                    }
                    ConstantPoolInfoEnum.CONSTANT_Integer -> LdcInst("I", (info as IntegerCp).`val`)
                    ConstantPoolInfoEnum.CONSTANT_Float -> LdcInst("F", (info as FloatCp).`val`)
                    ConstantPoolInfoEnum.CONSTANT_Class -> LdcInst("L", Utils.getString(constantPool, (info as ClassCp).nameIndex))
                    else -> throw IllegalStateException()
                }
            }
            0x13 -> {
                val lwIndex = stm.readUnsignedShort()
                val lwInfo = constantPool.infos[lwIndex - 1]
                when (lwInfo.infoEnum) {
                    ConstantPoolInfoEnum.CONSTANT_String -> {
                        val stringIndex = (lwInfo as StringCp).stringIndex
                        val string = Utils.getString(constantPool, stringIndex)
                        return LdcWInst("Ljava/lang/String", string)
                    }
                    ConstantPoolInfoEnum.CONSTANT_Integer -> return LdcWInst("I", (lwInfo as IntegerCp).`val`)
                    ConstantPoolInfoEnum.CONSTANT_Float -> return LdcWInst("F", (lwInfo as FloatCp).`val`)
                    ConstantPoolInfoEnum.CONSTANT_Class -> return LdcWInst("L", lwInfo)
                }
                throw IllegalStateException()
            }
            0x14 -> {
                val ldwIdx = stm.readUnsignedShort()
                val ldwInfo = constantPool.infos[ldwIdx - 1]
                when (ldwInfo.infoEnum) {
                    ConstantPoolInfoEnum.CONSTANT_Double -> return Ldc2wInst(null, (ldwInfo as DoubleCp).`val`)
                    ConstantPoolInfoEnum.CONSTANT_Long -> return Ldc2wInst((ldwInfo as LongCp).`val`, null)
                }
                throw IllegalStateException(ldwInfo.toString())
            }

            //五、load系列
            //5.1、load系列A
            //该系列命令负责把本地变量的送到栈顶。这里的本地变量不仅可以是数值类型，还可以是引用类型。
            0x15 -> ILoadNInst(stm.readUnsignedByte())  //从局部变量表加载一个int类型值到操作数栈中
            0x16 -> LLoadInst(stm.readUnsignedByte())   //long型
            0x17 -> FLoadInst(stm.readUnsignedByte())   //float型
            0x18 -> DLoadInst(stm.readUnsignedByte())   //double型
            0x19 -> ALoadInst(stm.readUnsignedByte())   //引用类型
            0x1a -> ILoad0Inst()
            0x1b -> ILoad1Inst()
            0x1c -> ILoad2Inst()
            0x1d -> ILoad3Inst()
            0x1e -> LLoad0Inst()
            0x1f -> LLoad1Inst()
            0x20 -> LLoad2Inst()
            0x21 -> LLoad3Inst()
            0x22 -> FLoad0Inst()
            0x23 -> FLoad1Inst()
            0x24 -> FLoad2Inst()
            0x25 -> FLoad3Inst()
            0x26 -> DLoad0Inst()
            0x27 -> DLoad1Inst()
            0x28 -> DLoad2Inst()
            0x29 -> DLoad3Inst()
            0x2a -> ALoad0Inst()
            0x2b -> ALoad1Inst()
            0x2c -> ALoad2Inst()
            0x2d -> ALoad3Inst()
            //5.2、load系列B
            //该系列命令负责把数组的某项送到栈顶。该命令根据栈里内容来确定对哪个数组的哪项进行操作。
            0x2e -> IALoadInst()    //
            0x2f -> LALoadInst()
            0x30 -> FALoadInst()
            0x31 -> DALoadInst()
            0x32 -> AALoadInst()
            0x33 -> BAloadInst()
            0x34 -> CAloadInst()
            0x35 -> SALoadInst()

            //六、store系列
            //该系列命令负责把栈顶的值存入本地变量。这里的本地变量不仅可以是数值类型，还可以是引用类型。
            0x36 -> IStoreNInst(stm.readUnsignedByte())
            0x37 -> LStoreNInst(stm.readUnsignedByte())
            0x38 -> FStoreNInst(stm.readUnsignedByte())
            0x39 -> DStoreNInst(stm.readUnsignedByte())
            0x3a -> AStoreInst(stm.readUnsignedByte())
            0x3b -> IStore0Inst()
            0x3c -> IStore1Inst()
            0x3d -> IStore2Inst()
            0x3e -> IStore3Inst()
            0x3f -> LStore0Inst()
            0x40 -> LStore1Inst()
            0x41 -> LStore2Inst()
            0x42 -> LStore3Inst()
            0x43 -> FStore0Inst()
            0x44 -> FStore1Inst()
            0x45 -> FStore2Inst()
            0x46 -> FStore3Inst()
            0x47 -> DStore0Inst()
            0x48 -> DStore1Inst()
            0x49 -> DStore2Inst()
            0x4a -> DStore3Inst()
            0x4b -> AStore0Inst()
            0x4c -> AStore1Inst()
            0x4d -> AStore2Inst()
            0x4e -> AStore3Inst()
            0x4f -> IAStoreInst()
            0x50 -> LAStoreInst()
            0x51 -> FAStoreInst()
            0x52 -> DAStoreInst()
            0x53 -> AAStoreInst()
            0x54 -> BAStoreInst()
            0x55 -> CAStoreInst()
            0x56 -> SAStoreInst()

            //七、pop系列
            //该系列命令似乎只是简单对栈顶进行操作
            0x57 -> PopInst()       //将栈顶数值弹出 (数值不能是long或double类型的)
            0x58 -> Pop2Inst()      //将栈顶的一个（long或double类型的)或两个数值弹出
            0x59 -> DupInst()
            0x5a -> DupX1Inst()
            0x5b -> DupX2Inst()
            0x5c -> Dup2Inst()
            0x5d -> Dup2X1Inst()
            0x5e -> Dup2X2Inst()
            0x5f -> SwapInst()
            0x60 -> IAddInst()
            0x61 -> LAddInst()
            0x62 -> FAddInst()
            0x63 -> DAddInst()
            0x64 -> ISubInst()
            0x65 -> LSubInst()
            0x66 -> FSubInst()
            0x67 -> DSubInst()
            0x68 -> IMulInst()
            0x69 -> LMulInst()
            0x6a -> FMulInst()
            0x6b -> DMulInst()
            0x6c -> IDivInst()
            0x6d -> LDivInst()
            0x6e -> FDivInst()
            0x6f -> DDivInst()
            0x70 -> IRemInst()
            0x71 -> LRemInst()
            0x72 -> FRemInst()
            0x73 -> DRemInst()
            0x74 -> INegInst()
            0x75 -> LNegInst()
            0x76 -> FNegInst()
            0x77 -> DNegInst()
            0x78 -> IShlInst()
            0x79 -> LShlInst()
            0x7a -> IShrInst()
            0x7b -> LShrInst()
            0x7c -> IUShrInst()
            0x7d -> LUShrInst()
            0x7e -> IAndInst()
            0x7f -> LAndInst()
            0x80 -> IOrInst()
            0x81 -> LOrInst()
            0x82 -> IXOrInst()
            0x83 -> LXOrInst()
            0x84 -> IIncInst(stm.readUnsignedByte(), stm.readByte().toInt())
            0x85 -> I2lInst()
            0x86 -> I2fInst()
            0x87 -> I2dInst()
            0x88 ->                 // l2i
                L2iInst()
            0x89 ->                 // l2f
                L2fInst()
            0x8a ->                 // l2d
                L2dInst()
            0x8b -> F2iInst()
            0x8c -> F2lInst()
            0x8d -> F2dInst()
            0x8e -> D2iInst()
            0x8f -> D2lInst()
            0x90 -> D2fInst()
            0x91 -> I2bInst()
            0x92 -> I2cInst()
            0x93 -> I2sInst()
            0x94 -> LCmpInst()
            0x95 -> FCmpLInst()
            0x96 -> FCmpGInst()
            0x97 -> DCmpLInst()
            0x98 -> DCmpGInst()
            0x99 -> IfEqInst(stm.readShort().toInt())
            0x9a -> IfNeInst(stm.readShort().toInt())
            0x9b -> IfLtInst(stm.readShort().toInt())
            0x9c -> IfGeInst(stm.readShort().toInt())
            0x9d -> IfGtInst(stm.readShort().toInt())
            0x9e -> IfLeInst(stm.readShort().toInt())
            0x9f -> IfICmpEqInst(stm.readShort().toInt())
            0xa0 -> IfICmpNeInst(stm.readShort().toInt())
            0xa1 -> IfICmpLtInst(stm.readShort().toInt())
            0xa2 -> IfICmpGeInst(stm.readShort().toInt())
            0xa3 -> IfICmpGtInst(stm.readShort().toInt())
            0xa4 -> IfICmpLeInst(stm.readShort().toInt())
            0xa5 -> IfACmpEqInst(stm.readShort().toInt())
            0xa6 -> IfACmpNeInst(stm.readShort().toInt())
            0xa7 -> GotoInst(stm.readShort())
            0xa8 -> throw UnsupportedOperationException("jsr")
            0xa9 -> throw UnsupportedOperationException("ret")
            0xaa -> {
                var offset = 1
                val padding = stm.readPadding()
                offset += padding
                val tsDefault = stm.readInt()
                val tsLow = stm.readInt()
                val tsHigh = stm.readInt()
                offset += 12
                val tsOffsetByteLength = (tsHigh - tsLow + 1) * 4
                val map: MutableMap<Int, Int> = LinkedHashMap()
                var i = tsLow
                while (i <= tsHigh) {
                    map[i] = stm.readInt()
                    i++
                }
                offset += tsOffsetByteLength
                TableSwitchInst(offset, tsDefault, tsLow, tsHigh, map)
            }
            0xab -> {
                var lsOffset = 1
                val lsPadding = stm.readPadding()
                lsOffset += lsPadding
                val lsDef = stm.readInt()
                lsOffset += 4
                val lsPairsCnt = stm.readInt()
                lsOffset += 4
                val lsPairsLen = lsPairsCnt * 2 * 4
                val lsMap: MutableMap<Int, Int> = LinkedHashMap()
                var i = 0
                while (i < lsPairsCnt) {
                    lsMap[stm.readInt()] = stm.readInt()
                    i++
                }
                lsOffset += lsPairsLen
                LookupSwitchInst(lsOffset, lsDef, lsPairsCnt, lsMap)
            }
            0xac -> IReturnInst()
            0xad -> LReturnInst()
            0xae -> FReturnInst()
            0xaf -> DReturnInst()
            0xb0 -> AReturnInst()
            0xb1 -> ReturnInst()
            0xb2 -> {
                val gsIndex = stm.readUnsignedShort()
                GetStaticInst(
                        Utils.getClassNameByFieldDefIdx(constantPool, gsIndex),
                        Utils.getMethodNameByFieldDefIdx(constantPool, gsIndex),
                        Utils.getMethodTypeByFieldDefIdx(constantPool, gsIndex)
                )
            }
            0xb3 -> {
                val psIndex = stm.readUnsignedShort()
                PutStaticInst(
                        Utils.getClassNameByFieldDefIdx(constantPool, psIndex),
                        Utils.getMethodNameByFieldDefIdx(constantPool, psIndex),
                        Utils.getMethodTypeByFieldDefIdx(constantPool, psIndex)
                )
            }
            0xb4 -> {
                val gfIndex = stm.readUnsignedShort()
                GetFieldInst(
                        Utils.getClassNameByFieldDefIdx(constantPool, gfIndex),
                        Utils.getMethodNameByFieldDefIdx(constantPool, gfIndex),
                        Utils.getMethodTypeByFieldDefIdx(constantPool, gfIndex)
                )
            }
            0xb5 -> {
                val pfIndex = stm.readUnsignedShort()
                PutFieldInst(
                        Utils.getClassNameByFieldDefIdx(constantPool, pfIndex),
                        Utils.getMethodNameByFieldDefIdx(constantPool, pfIndex),
                        Utils.getMethodTypeByFieldDefIdx(constantPool, pfIndex)
                )
            }
            0xb6 -> {
                val ivIndex = stm.readUnsignedShort()
                InvokeVirtualInst(
                        Utils.getClassNameByMethodDefIdx(constantPool, ivIndex),
                        Utils.getMethodNameByMethodDefIdx(constantPool, ivIndex),
                        Utils.getMethodTypeByMethodDefIdx(constantPool, ivIndex)
                )
            }
            0xb7 -> {
                val isIndex = stm.readUnsignedShort()
                InvokeSpecialInst(
                        Utils.getClassNameByMethodDefIdx(constantPool, isIndex),
                        Utils.getMethodNameByMethodDefIdx(constantPool, isIndex),
                        Utils.getMethodTypeByMethodDefIdx(constantPool, isIndex)
                )
            }
            0xb8 -> {
                val mdIdx = stm.readUnsignedShort()
                InvokeStaticInst(
                        Utils.getClassNameByMethodDefIdx(constantPool, mdIdx),
                        Utils.getMethodNameByMethodDefIdx(constantPool, mdIdx),
                        Utils.getMethodTypeByMethodDefIdx(constantPool, mdIdx)
                )
            }
            0xb9 -> {
                val iiIdx = stm.readUnsignedShort()
                InvokeInterfaceInst(
                        Utils.getClassNameByIMethodDefIdx(constantPool, iiIdx),
                        Utils.getMethodNameByIMethodDefIdx(constantPool, iiIdx),
                        Utils.getMethodTypeByIMethodDefIdx(constantPool, iiIdx),
                        stm.readUnsignedByte(),
                        stm.readUnsignedByte()
                )
            }
            0xba -> {
                val idsrIdx = stm.readUnsignedShort()
                val idInfo = constantPool.infos[idsrIdx - 1]
                val invokeDynamic = idInfo as InvokeDynamic
                val bmaIdx = invokeDynamic.bootstrapMethodAttrIndex
                val idName = Utils.getNameByNameAndTypeIdx(constantPool, invokeDynamic.nameAndTypeIndex)
                val idType = Utils.getTypeByNameAndTypeIdx(constantPool, invokeDynamic.nameAndTypeIndex)
                stm.readUnsignedByte()
                stm.readUnsignedByte()
                InvokeDynamicInst(idName, idType, bmaIdx)
            }
            0xbb -> NewInst(Utils.getClassName(constantPool, stm.readUnsignedShort()))
            0xbc -> NewArrayInst(stm.readUnsignedByte())
            0xbd -> ANewArrayInst(Utils.getClassName(constantPool, stm.readUnsignedShort()))
            0xbe -> ArrayLengthInst()
            0xbf -> AThrowInst()
            0xc0 -> CheckcastInst(Utils.getClassName(constantPool, stm.readUnsignedShort()))
            0xc1 -> {
                val ioClazzIdx = stm.readUnsignedShort()
                InstanceofInst(Utils.getClassName(constantPool, ioClazzIdx))
            }
            0xc2 -> MonitorEnterInst()
            0xc3 -> MonitorExitInst()
            0xc4 -> {
                val wideOpcode = stm.readUnsignedByte()
                when (wideOpcode) {
                    0x84 -> return WideInst(6, IIncInst(stm.readUnsignedShort(), stm.readShort().toInt()))
                    0x15 -> return WideInst(4, ILoadNInst(stm.readUnsignedShort()))
                    0x17 -> return WideInst(4, FLoadInst(stm.readUnsignedShort()))
                    0x19 -> return WideInst(4, ALoadInst(stm.readUnsignedShort()))
                    0x16 -> return WideInst(4, LLoadInst(stm.readUnsignedShort()))
                    0x18 -> return WideInst(4, DLoadInst(stm.readUnsignedShort()))
                    0x36 -> return WideInst(4, IStoreNInst(stm.readUnsignedShort()))
                    0x38 -> return WideInst(4, FStoreNInst(stm.readUnsignedShort()))
                    0x3a -> return WideInst(4, AStoreInst(stm.readUnsignedShort()))
                    0x37 -> return WideInst(4, LStoreNInst(stm.readUnsignedShort()))
                    0x39 -> return WideInst(4, DStoreNInst(stm.readUnsignedShort()))
                    0xa9 -> throw UnsupportedOperationException()
                }
                throw UnsupportedOperationException("wide op $wideOpcode")
            }
            0xc5 -> MultiANewArrayInst(stm.readUnsignedShort(), stm.readUnsignedByte())
            0xc6 -> IfNullInst(stm.readShort().toInt())
            0xc7 -> IfNonNullInst(stm.readShort().toInt())
            0xc8 ->                 // goto_w
                GotoWInst(stm.readInt())
            0xc9 -> throw UnsupportedOperationException()
            else -> throw IllegalArgumentException("找不到对应指令")
        }
    }
}