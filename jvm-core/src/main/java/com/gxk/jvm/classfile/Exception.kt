package com.gxk.jvm.classfile

//TODO 学习finally
data class Exception(
        val startPc: Int,   //code[]指令的某一行, 代表开始进入异常检测范围
        val endPc: Int,     //异常结束的那一行
        val handlerPc: Int, //遇到异常时, 跳转到的行数, 也就是catch里的内容
        val clazz: String?  //抛出的异常类型, 如果是null, 则代表捕获所有异常, eg:finally
)