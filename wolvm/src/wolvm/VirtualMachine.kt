package wolvm

import utils.*
import java.io.*
import java.lang.*

const val version: String = "1.0.0.0"
val wolVoid: wolClass = wolClass()
var mainstack: Stack = Stack()
var expressions: MutableMap<String, VMExpression> = mutableMapOf()

fun throwVMException(message: String, position: Int, type: ExceptionType) = println("$type. In position $position. $message")

fun main(args: Array<String>)
{
    if (args.isEmpty())
    {
        print("World of Legends Virtual Machine v$version\nAuthor: snaulX\nCopyright (c) 2019")
    }
    else
    {
        when (args[0]) {
            "-info" -> {
                print("World of Legends Virtual Machine v$version\nAuthor: snaulX\nCopyright (c) 2019")
            }
            "-run" -> {
                //pass
            }
            "-help" -> {
                println("World of Legends Virtual Machine Helper")
                println()
                println("Arguments:")
                println("-info ; print info about virtual machine")
                println("<full file name> ; run build-file")
            }
            else -> {
                lateinit var reader: FileReader
                try
                {
                    reader = FileReader(args[0])
                }
                catch (e: FileNotFoundException)
                {
                    throwVMException("File by full name ${args[0]} not found", 0, ExceptionType.FileNotFoundException)
                }
                val input: String = reader.readText()

                //main cycle
                var position: Int = 0
                var current: Char = input[position]
                while (position < input.length)
                {
                    while (current.isWhitespace()) //skip whitespaces
                    {
                        try {
                            current = input[++position]
                        }
                        catch (ex: IndexOutOfBoundsException)
                        {
                            throwVMException("End of parsing not found", position, ExceptionType.BLDSyntaxException)
                            break
                        }
                    }
                    var buffer: StringBuilder = StringBuilder()
                    while (!current.isWhitespace()) //get word
                    {
                        try {
                            buffer.append(current)
                            current = input[++position]
                        }
                        catch (ex: IndexOutOfBoundsException)
                        {
                            throwVMException("End of parsing not found", position, ExceptionType.BLDSyntaxException)
                        }
                    }
                    when (buffer.toString())
                    {
                        "_loads" -> {
                            current = input[++position]
                            if (current == '{')
                            {
                                //valid
                            }
                            else
                            {
                                throwVMException("Start of _loads block not found", position, ExceptionType.BLDSyntaxException)
                            }
                        }
                        "main" -> {
                            current = input[++position]
                            if (current == '{')
                            {
                                while (current != '}') {
                                    try {
                                        buffer.append(current)
                                        current = input[++position]
                                    } catch (ex: IndexOutOfBoundsException) {
                                        throwVMException("End of script not found", position, ExceptionType.BLDSyntaxException)
                                    }
                                }
                                parse(buffer.toString())
                            }
                            else
                            {
                                throwVMException("Start of script not found", position, ExceptionType.BLDSyntaxException)
                            }
                        }
                        "stack" -> {
                            current = input[++position]
                            if (current == '{')
                            {
                                val start = {
                                    while (current != '}') {
                                        try {
                                            buffer.append(current)
                                            current = input[++position]
                                        } catch (ex: IndexOutOfBoundsException) {
                                            throwVMException("End of stack not found", position, ExceptionType.BLDSyntaxException)
                                        }
                                    }
                                }
                                if (input[position + 1] == ';')
                                {
                                    run(start) //kotlin why you haven`t 'goto' ((
                                }
                                Stack.parse(buffer.toString())
                            }
                            else
                            {
                                throwVMException("Start of stack not found", position, ExceptionType.BLDSyntaxException)
                            }
                        }
                        "end" -> return
                        else ->
                            throwVMException("Unknown keyword $buffer", position, ExceptionType.BLDSyntaxException)
                    }
                }
            }
        }
    }
}
