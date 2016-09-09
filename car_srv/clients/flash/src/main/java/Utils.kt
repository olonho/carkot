import com.martiansoftware.jsap.FlaggedOption
import com.martiansoftware.jsap.JSAP
import com.martiansoftware.jsap.JSAPResult
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.*

fun saveFileConfig(actualValues: Map<String, String>) {
    var dataToFile = ""
    actualValues.forEach { e -> dataToFile += ("${e.key}:${e.value}\n") }
    val file = File(configFileName)
    if (!file.exists()) {
        try {
            file.createNewFile()
        } catch (e: IOException) {
            println("error creating config file. parameters don't saved")
            e.printStackTrace()
            return
        }
    }
    PrintWriter(file).use { printWriter ->
        printWriter.write(dataToFile)
        printWriter.flush()
    }
}

fun getActualValue(argName: String, commandLineConfig: JSAPResult, fileConfig: Map<String, String>, defaultValue: String = ""): String {
    return if (commandLineConfig.getString(argName) != null) {
        commandLineConfig.getString(argName)
    } else {
        fileConfig.getOrElse(argName, {
            defaultValue
        })
    }
}

fun readFileConfig(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    try {
        Scanner(File(configFileName)).use { scanner ->
            while (scanner.hasNext()) {
                val currentString = scanner.nextLine()
                val keyValuePair = currentString.split(":")
                if (keyValuePair.size != 2) {
                    continue
                } else {
                    result.put(keyValuePair[0], keyValuePair[1])
                }
            }
        }
    } catch (e: IOException) {
    }
    return result
}

fun setOptions(jsap: JSAP) {
    val optHost = FlaggedOption("host").setStringParser(JSAP.STRING_PARSER).setRequired(false).setShortFlag('h').setLongFlag("host")
    optHost.help = "write here only ip address. domain name (e.g. vk.com is incorrect)"
    val optPort = FlaggedOption("port").setStringParser(JSAP.STRING_PARSER).setRequired(false).setShortFlag('p').setLongFlag("port")
    val optBinPath = FlaggedOption("flash").setStringParser(JSAP.STRING_PARSER).setRequired(false).setShortFlag('f').setLongFlag("flash")
    val optHelp = FlaggedOption("help").setStringParser(JSAP.BOOLEAN_PARSER).setRequired(false).setDefault("false").setLongFlag("help")
    jsap.registerParameter(optHost)
    jsap.registerParameter(optPort)
    jsap.registerParameter(optBinPath)
    jsap.registerParameter(optHelp)
}

fun isCorrectArguments(actualValues: Map<String, String>): Boolean {
    //host and flash file its required.
    val host = actualValues.getOrElse("host", { "" })
    val flashFilePath = actualValues.getOrElse("flash", { "" })

    if (host.equals("") || flashFilePath.equals("")) {
        println("incorrect args (host/flash file must be initialized)")
        return false
    }
    val file = File(flashFilePath)
    if (!file.exists()) {
        println("file ${file.absoluteFile} not exist")
        return false
    }
    val ipRegex = Regex("^(?:[0-9]{1,3}.){3}[0-9]{1,3}$")
    if (!ipRegex.matches(host)) {
        println("incorrect server address.")
        return false
    }
    return true
}