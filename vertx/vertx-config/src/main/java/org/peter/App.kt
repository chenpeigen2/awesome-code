package org.peter

import java.io.File

fun writeFile(text: String, destFile: String) {
    val f = File(destFile)
    if (!f.exists()) {
        f.createNewFile()
    }
    f.writeText(text)
}

fun readFile(destFile: String): String {
    val f = File(destFile)
    if (!f.exists()) {
        f.createNewFile()
    }
    return f.readText()
}


fun main() {
    val json = """
        {
          "port": 8083,
          "max-delay-milliseconds": 800,
          "worker-count": 1234567
        }
    """.trimIndent()
    writeFile(json, "./config/config.json")

    val text = readFile("./config/config.json")
    println(text)
}