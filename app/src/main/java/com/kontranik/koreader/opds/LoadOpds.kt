package com.kontranik.koreader.opds

import android.util.Log
import com.kontranik.koreader.opds.model.Opds
import com.kontranik.koreader.opds.model.OpenSearchDescription
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder


object LoadOpds {

    @Throws(XmlPullParserException::class, IOException::class, MalformedURLException::class)
    fun loadXmlFromNetwork(urlString: String): Opds? {

        Log.d("OPDS", "loadXmlFromNetwork: " + urlString)

        val result = downloadUrl(urlString)?.use { stream ->
            OpdsXmlParser().parse(stream)
        }
        return  result
    }

    @Throws(XmlPullParserException::class, IOException::class, MalformedURLException::class)
    fun loadSearchFromNetwork(urlString: String): OpenSearchDescription? {
        val result = downloadUrl(urlString)?.use { stream ->
            OpenSearchXmlParser().parse(stream)
        }
        return  result
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    @Throws(IOException::class, java.net.MalformedURLException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        var resourceUrl: URL
        var base: URL
        var next: URL
        var conn: HttpURLConnection
        var location: String
        var times = 0
        var url = urlString

        while (true) {
            times++

            if (times > 3) throw IOException("Stuck in redirect loop")
            resourceUrl = URL(url)

            conn = resourceUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            conn.instanceFollowRedirects =
                false // Make the logic below easier to detect redirections
            conn.setRequestProperty("User-Agent", "Mozilla/5.0...")
            when (conn.responseCode) {
                HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP -> {
                    location = conn.getHeaderField("Location")
                    location = URLDecoder.decode(location, "UTF-8")
                    base = URL(url)
                    next = URL(base, location) // Deal with relative URLs
                    url = next.toExternalForm()
                    continue
                }
            }
            break
        }
        return conn.inputStream
    }
}