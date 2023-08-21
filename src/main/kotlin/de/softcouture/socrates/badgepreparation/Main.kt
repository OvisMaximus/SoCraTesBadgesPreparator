package de.softcouture.socrates.badgepreparation
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.net.URLEncoder

import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

const val QR_CODE_GEN_URL = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="


fun downloadFile(url: URL, outputFileName: String) {
    url.openStream().use {
        Channels.newChannel(it).use { rbc ->
            FileOutputStream(outputFileName).use { fos ->
                fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
            }
        }
    }
}

fun createQrCodeUrl(qrCodeData: String):String {
   val encodedUrl = URLEncoder.encode(qrCodeData, "UTF-8")
   return QR_CODE_GEN_URL + encodedUrl
}

fun main(args: Array<String>) {

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    //println("Program arguments: ${args.joinToString()}")

    val inputFile = args[0]
    val outputFile = args[1]
    println("in='$inputFile' out='$outputFile'")
    val outputData = mutableListOf<List<String>>()

    val csvReader = csvReader{
        charset = "UTF-8"
        quoteChar = '"'
        delimiter = ';'
        escapeChar = '\\'
    }

    csvReader.open(inputFile) {

        readAllWithHeaderAsSequence().forEach { row ->
            val attendee = AttendeeRecord(row)
            println(attendee)

            for (social in attendee.getSocialList()) {

                val filename = social.second
                val downloadUrl = createQrCodeUrl(social.first)
                if (! File(filename).isFile)
                    try {
                        println("Fetching $filename from $downloadUrl")
                        downloadFile(URL(downloadUrl), filename)
                    } catch (e: Exception) {
                        println("when trying to download qr for ${social}: " + e)
                    }
            }
            outputData.add(attendee.getCsvLineData())
        }
        csvWriter {
            charset = "UTF-8"
            delimiter = ',' // when using semi colon as delimeter, the library messed up escaping...
        }.open(outputFile) {
            writeRow(AttendeeRecord.getCsvColumnHeaders())
            outputData.forEach { row ->
                writeRow(row)
            }
        }


    }

}

