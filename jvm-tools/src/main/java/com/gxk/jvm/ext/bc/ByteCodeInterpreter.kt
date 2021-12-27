package com.gxk.jvm.ext.bc

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.interpret.Interpreter.loop
import java.nio.file.Files
import java.nio.file.Paths
import java.io.IOException
import java.util.stream.Collectors
import java.util.HashMap
import java.util.Locale
import com.gxk.jvm.instruction.constants.IConst0Inst
import com.gxk.jvm.instruction.constants.IConst1Inst
import com.gxk.jvm.instruction.stores.IStore0Inst
import com.gxk.jvm.instruction.stores.IStore1Inst
import com.gxk.jvm.instruction.stores.IStore2Inst
import com.gxk.jvm.instruction.loads.ILoad0Inst
import com.gxk.jvm.instruction.loads.ILoad1Inst
import com.gxk.jvm.instruction.loads.ILoad2Inst
import com.gxk.jvm.instruction.constants.BiPushInst
import com.gxk.jvm.instruction.comparisons.IfICmpGtInst
import com.gxk.jvm.instruction.math.IAddInst
import com.gxk.jvm.instruction.math.IIncInst
import com.gxk.jvm.instruction.extended.GotoInst
import com.gxk.jvm.interpret.Interpreter
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.MetaSpace
import com.gxk.jvm.rtda.Thread
import com.gxk.jvm.rtda.heap.Method

object ByteCodeInterpreter {
    fun interpreter(file: String?, vararg args: Int) {
        // parse
        val rawLines: List<String>
        rawLines = try {
            Files.readAllLines(Paths.get(file))
        } catch (e: IOException) {
            println("file not found ?")
            return
        }
        if (rawLines.isEmpty()) {
            println("empty file")
        }
        val lines = rawLines.stream()
                .map { it: String -> it.replace(" +".toRegex(), " ") } // 多个空格合并
                .filter { it: String -> it.length > 0 }
                .collect(Collectors.toList())
        val header = lines[0]
        val split = header.split(" ").toTypedArray()
        val stacks = split[1].toInt()
        val locals = split[2].toInt()
        val argsCnt = split[3].toInt()

        // interfaceInit method args
        val methodArgs: MutableMap<Int, Int> = HashMap(argsCnt)
        for (i in 0 until argsCnt) {
            methodArgs[i] = 0
        }
        if (args.size > 0) {
            for (i in 0 until Math.min(args.size, argsCnt)) {
                methodArgs[i] = args[i]
            }
        }
        val instructionMap: MutableMap<Int, Instruction> = HashMap()
        for (i in 1 until lines.size) {
            val raw = lines[i]
            val terms = raw.split(" ").toTypedArray()
            val pc = terms[0].toInt()
            val inst = terms[1]
            var instruction: Instruction? = null
            when (inst.lowercase(Locale.getDefault())) {
                "iconst_0" -> instruction = IConst0Inst()
                "iconst_1" -> instruction = IConst1Inst()
                "istore_0" -> instruction = IStore0Inst()
                "istore_1" -> instruction = IStore1Inst()
                "istore_2" -> instruction = IStore2Inst()
                "iload_0" -> instruction = ILoad0Inst()
                "iload_1" -> instruction = ILoad1Inst()
                "iload_2" -> instruction = ILoad2Inst()
                "bipush" -> instruction = BiPushInst(terms[2].toByte())
                "if_icmpgt" -> instruction = IfICmpGtInst(terms[2].toInt())
                "iadd" -> instruction = IAddInst()
                "iinc" -> instruction = IIncInst(terms[2].toInt(), terms[3].toInt())
                "goto" -> instruction = GotoInst(terms[2].toShort())
                "ireturn" -> instruction = IReturnInst()
            }
            if (instruction == null) {
                println("parse file failed. raw : $raw")
                return
            }
            instructionMap[pc] = instruction
        }
        val thread = Thread(2)
        MetaSpace.main = thread
        val method = Method(1, "main", "()I", stacks, locals, instructionMap, null!!, null!!)
        val frame = Frame(method)
        thread.pushFrame(frame)

        // args
        for (i in 0 until argsCnt) {
            frame.setInt(i, methodArgs[i]!!)
        }
        loop(thread)
    }
}