package com.gxk.jvm

import com.gxk.jvm.Args
import java.util.Objects
import com.gxk.jvm.util.EnvHolder
import com.gxk.jvm.util.Logger
import java.util.zip.ZipEntry
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalArgumentException
import kotlin.Throws
import java.lang.StringBuilder
import java.util.zip.ZipFile

/**
 * java -cp target/mini.jar com.gxk.Main xxxxxx
 */
public class Args {
    var verbose = false
    var verboseTrace = false
    var verboseCall = false
    var verboseClass = false
    var verboseDebug = false
    var classpath = "."
    var clazz: String? = null
    var args = arrayOfNulls<String>(0)

    companion object {
        private const val MINUS_VERBOSE = "-verbose"
        private const val MINUS_VERBOSE_TRACE = "-verbose:trace"
        private const val MINUS_VERBOSE_CALL = "-verbose:call"
        private const val MINUS_VERBOSE_CLASS = "-verbose:class"
        private const val MINUS_VERBOSE_DEBUG = "-verbose:debug"
        private const val MINUS_COLOR_RED = "-Xcolor:red"
        private const val MINUS_COLOR_GREEN = "-Xcolor:green"
        private const val MINUS_COLOR_YELLOW = "-Xcolor:yellow"
        private const val MINUS_CP = "-cp"
        private const val MINUS_JAR = "-jar"
        fun parseArgs(vararg cliArgs: String): Args {
            val args = Args()
            if (cliArgs.size == 1) {
                args.clazz = cliArgs[0]
                return args
            }
            var pi = 0
            var i = 0
            while (i < cliArgs.size) {
                val tmp = cliArgs[i]
                if (tmp == MINUS_CP) {
                    val cp = cliArgs[++i]
                    if (args.classpath == ".") {
                        args.classpath = cp
                    } else {
                        args.classpath = args.classpath + ":" + cp
                    }
                    args.clazz = cliArgs[++i]
                    pi = ++i
                    break
                }
                if (tmp == MINUS_JAR) {
                    val jar = cliArgs[++i]
                    args.classpath = args.classpath + ":" + jar
                    args.clazz = parseMainClass(jar)
                    pi = ++i
                    break
                }
                if (!tmp.startsWith("-")) {
                    args.clazz = tmp
                    pi = ++i
                    break
                }
                if (MINUS_VERBOSE == tmp) {
                    args.verbose = true
                    i++
                    continue
                }
                if (MINUS_VERBOSE_TRACE == tmp) {
                    args.verboseTrace = true
                    i++
                    continue
                }
                if (MINUS_VERBOSE_CALL == tmp) {
                    args.verboseCall = true
                    i++
                    continue
                }
                if (MINUS_VERBOSE_CLASS == tmp) {
                    args.verboseClass = true
                    i++
                    continue
                }
                if (MINUS_VERBOSE_DEBUG == tmp) {
                    args.verboseDebug = true
                    i++
                    continue
                }
                if (MINUS_COLOR_RED == tmp) {
                    Logger.fg = Logger.ANSI_RED
                    Logger.trace("testtest")
                    i++
                    continue
                }
                if (MINUS_COLOR_GREEN == tmp) {
                    Logger.PREFIX = "G"
                    Logger.fg = Logger.ANSI_GREEN
                    i++
                    continue
                }
                if (MINUS_COLOR_YELLOW == tmp) {
                    Logger.PREFIX = "Y"
                    Logger.fg = Logger.ANSI_YELLOW
                    i++
                    continue
                }
                i++
            }
            if (cliArgs.size > pi) {
                val programArgs = arrayOfNulls<String>(cliArgs.size - pi)
                System.arraycopy(cliArgs, pi, programArgs, 0, programArgs.size)
                args.args = programArgs
            }
            return args
        }

        private fun parseMainClass(mainJar: String): String {
            val userDir = System.getProperty("user.dir")
            val path = userDir + EnvHolder.FILE_SEPARATOR + mainJar
            try {
                ZipFile(path).use { file ->
                    val entry = file.getEntry("META-INF/MANIFEST.MF")
                    file.getInputStream(entry).use { `is` ->
                        var line: String
                        while (readLine(`is`).also { line = it!! } != null) {
                            if (line.startsWith("Main-Class: ")) {
                                return line.substring(12)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                throw IllegalArgumentException()
            }
            throw IllegalArgumentException("Not found main class")
        }

        @Throws(IOException::class)
        private fun readLine(`is`: InputStream): String? {
            val line = StringBuilder()
            var b = `is`.read()
            if (b < 0) {
                return null
            }
            while (b > 0) {
                var c = b.toChar()
                if (c == '\r' || c == '\n') {
                    break
                }
                if (c == '.') {
                    c = '/'
                }
                line.append(c)
                b = `is`.read()
            }
            return line.toString()
        }
    }
}