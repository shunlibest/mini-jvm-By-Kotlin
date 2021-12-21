package com.gxk.jvm.interpret

import com.gxk.jvm.instruction.Instruction
import com.gxk.jvm.rtda.Frame
import com.gxk.jvm.rtda.heap.Heap.findClass
import com.gxk.jvm.rtda.heap.Heap.registerClass
import com.gxk.jvm.rtda.MetaSpace
import com.gxk.jvm.rtda.Thread
import com.gxk.jvm.rtda.heap.Class
import com.gxk.jvm.rtda.heap.Instance
import com.gxk.jvm.rtda.heap.InstanceArray
import com.gxk.jvm.rtda.heap.Method
import com.gxk.jvm.util.*
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.*

object Interpreter {

    /**
     * 执行main方法
     * @param method Method
     * @param args Array<String?>
     */
    fun runMain(method: Method, args: Array<String?>) {
        val frame = Frame(method)
        val kArgs = arrayOfNulls<Instance>(args.size)
        for (i in args.indices) {
            kArgs[i] = Utils.str2Obj(args[i], frame.method.clazz.classLoader)
        }
        var arrClazz = findClass("[Ljava/lang/String;")
        if (arrClazz == null) {
            arrClazz = Class(1, "[Ljava/lang/String;", method.clazz.classLoader, null)
            registerClass(arrClazz.name, arrClazz)
        }
        val array = InstanceArray(arrClazz, kArgs)
        frame.setRef(0, array)
        execute(frame)
    }

    /**
     * 同步执行指定方法
     */
    fun execute(method: Method) {
        val env = MetaSpace.getMainEnv()
        val newFrame = Frame(method)
        // 传参
        val slots = method.argSlotSize
        if (slots > 0) {
            val old = env.topFrame()
            for (i in slots - 1 downTo 0) {
                newFrame[i] = old.pop()
            }
        }
        execute(newFrame)
    }

    /**
     * 同步执行栈帧
     */
    fun execute(newFrame: Frame) {
        //在主线程中, 把方法压入到栈里面
        val env = MetaSpace.getMainEnv()
        env.pushFrame(newFrame)
        newFrame.stat = Const.FAKE_FRAME
        do {
            val frame = env.topFrame()
            val instruction = frame.inst!!
            frame.nextPc += instruction.offset()
            traceBefore(instruction, frame)
            instruction.execute(frame)
            //      if (env.exception != null) {
//        Utils.handleException(env.exception);
//        env.exception = null;
//      }
        } while (newFrame.stat == Const.FAKE_FRAME)
    }


    fun loop(thread: Thread) {
        if (EnvHolder.debug) {
            try {
                println("正在初始化jdb...")
                DebugContextHolder.scanner = Scanner(System.`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        do {
            val frame = thread.topFrame()
            val pc = frame.nextPc
            val inst = frame.inst
            if (inst == null) {
                val sb = StringBuilder()
                sb.append(pc).append("\n")
                sb.append("class: ").append(frame.method.clazz.name).append("\n")
                sb.append("method: ").append(frame.method.name).append("\n")
                sb.append("methodDescriptor: ").append(frame.method.descriptor).append("\n")
                frame.method.instructionMap.forEach { (key: Int?, `val`: Instruction) -> sb.append(key).append(" ").append(`val`.format()).append("\n") }
                val str = sb.toString()
                System.err.println(str)
                throw IllegalStateException()
            }
            traceBefore(inst, frame)
            frame.nextPc += inst.offset()
            //      if (EnvHolder.debug) {
//        boolean cont = doDebug(inst, frame);
//        if (!cont) {
//          // restore
//          frame.nextPc -= inst.offset();
//          continue;
//        }
//      }
            try {
                inst.execute(frame)
            } catch (e: Exception) {
                e.printStackTrace()
                val name = frame.currentMethodFullName
                val msg = name + "(" + frame.getCurrentSource() + ":" + frame.currentLine + ")"
                println(msg)
                throw IllegalStateException()
            }
        } while (!thread.empty())
    }

    //  private boolean doDebug(Instruction inst, Frame frame) {
    //    if (DebugContextHolder.next) {
    //      if (!inst.format().startsWith("invoke")) {
    //        return true;
    //      }
    //      DebugContextHolder.next = false;
    //    }
    //
    //    Scanner scanner = DebugContextHolder.scanner;
    //    if (scanner == null) {
    //      Logger.error("reader init err in debug mode, debug mode closed");
    //      EnvHolder.debug = false;
    //      return false;
    //    }
    //    try {
    //      String promot = frame.thread.size() + " > ";
    //      System.out.print(promot);
    //      String line = scanner.nextLine();
    //      if (line == null || line.trim().isEmpty()) {
    //        return false;
    //      }
    //
    //      String cmd = line.trim().toLowerCase();
    //      if (!DebugContextHolder.running && !(Objects.equals("run", cmd) || Objects
    //          .equals("help", cmd))) {
    //        System.out.println(String.format("在使用 'run' 命令启动 VM 前, 命令 '%s' 是无效的", line));
    //        return false;
    //      }
    //      switch (cmd) {
    //        case "run":
    //          System.out.println(String.format("运行 %s", DebugContextHolder.mainClass));
    //          DebugContextHolder.running = true;
    //          break;
    //        case "help":
    //        case "h":
    //          System.out.println("print help");
    //          break;
    //        case "env":
    //          debugBefore(inst, frame);
    //          break;
    //        case "step":
    //        case "s":
    //          DebugContextHolder.step = true;
    //          break;
    //        case "next":
    //        case "n":
    //          DebugContextHolder.next = true;
    //          break;
    //        case "list":
    //        case "ls":
    //          frame.method.instructionMap.forEach((key, val) -> {
    //            String prefix = "     ";
    //            if (frame.getPc() == key) {
    //              prefix = "==>  ";
    //            }
    //            System.out.println(prefix + key + " " + val.format());
    //          });
    //          break;
    //        default:
    //          System.out.println("unsupport " + cmd);
    //          break;
    //      }
    //      if (!DebugContextHolder.isContinue()) {
    //        return false;
    //      }
    //    } catch (Exception e) {
    //      e.printStackTrace();
    //    }
    //    return true;
    //  }
    //打印trace
    private fun traceBefore(inst: Instruction, frame: Frame) {
        if (EnvHolder.verboseDebug) {
            debugBefore(inst, frame)
        }
        // verboseTrace
        if (EnvHolder.verboseTrace) {
            trace(inst, frame)
        }
        // verboseCall
        if (EnvHolder.verboseCall) {
            call(inst, frame)
        }
    }

    private fun call(inst: Instruction, frame: Frame) {
        if (!inst.format().startsWith("invoke")) {
            return
        }
        val space = genSpace((frame.thread.size() - 1) * 2)
        Logger.trace(space + Integer.toString(frame.pc) + " " + inst.format())
    }

    private fun trace(inst: Instruction, frame: Frame) {
        val space = genSpace((frame.thread.size() - 1) * 2)
        Logger.trace(space + Integer.toString(frame.pc) + " " + inst.format())
    }

    fun debugBefore(inst: Instruction, frame: Frame) {
        val space = genSpace((frame.thread.size() - 1) * 2)
        Logger.debug(space + Integer.toString(frame.thread.size()) + " <> " + frame.method.name + "_" + frame.method.descriptor +
                " ==============================")
        Logger.debug(inst.debug(space + frame.pc + " "))
        Logger.debug(frame.debugNextPc(space))
        Logger.debug(frame.debugLocalVars(space))
        Logger.debug(frame.debugOperandStack(space))
        Logger.debug("$space---------------------")
        Logger.debug("""
    $space
    
    """.trimIndent())
    }

    fun genSpace(size: Int): String {
        var x = ""
        for (i in 0 until size) {
            x = "$x "
        }
        return x
    }
}