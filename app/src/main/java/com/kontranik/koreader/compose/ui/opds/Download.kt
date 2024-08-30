package com.kontranik.koreader.compose.ui.opds

import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.utils.UrlHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

fun downloadOpdsEntry(
    entry: Entry,
    link: Link,
    startUrl: String
) {
    if (link.href == null) return
    try {
        val subdir = (entry.author?.name ?: "unknown")
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + File.separator + subdir
        )
        if (!dir.exists()) dir.mkdirs()
        val fileName = "${entry.title}.${link.getExtension()}"
        val file = File(dir, fileName)

        Toast.makeText(
            KoReaderApplication.getContext(),
            "Download $fileName start",
            Toast.LENGTH_SHORT
        ).show()
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            var error: String? = null
            try {
                val url = URL(UrlHelper.getUrl(link.href!!, startUrl))
                connection =
                    url.openConnection() as HttpURLConnection
                connection.connect()

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i(
                        "DownloadTask",
                        "Response ${connection.responseCode}"
                    )
                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    // val fileLength = connection.contentLength

                    // download the file
                    input = connection.inputStream
                    output =
                        FileOutputStream(file, false)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data)
                            .also { count = it } != -1
                    ) {
                        total += count.toLong()
                        // Log.d("DownloadTask", total.toString())
                        // publishing the progress....
                        //if (fileLength > 0) // only if total length is known
                        //publishProgress((total * 100 / fileLength).toInt())
                        output.write(data, 0, count)
                    }
                }
            } catch (e: Exception) {
                Log.e("DownloadTask", e.localizedMessage, e)
                error = e.localizedMessage
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (ignored: IOException) {
                }
                connection?.disconnect()
            }
            handler.post {
                if (error != null)
                    Toast.makeText(
                        KoReaderApplication.getContext(),
                        "Download $fileName error:\n$error",
                        Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(
                        KoReaderApplication.getContext(),
                        "Download completed: $fileName",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    } catch (e: Exception) {
        Log.e("DOWNLOAD", e.localizedMessage, e)
    }
}