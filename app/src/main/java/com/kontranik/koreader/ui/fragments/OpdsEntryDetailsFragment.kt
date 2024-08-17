package com.kontranik.koreader.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.compose.ui.opds.OpdsEntryDetailsScreen
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


class OpdsEntryDetailsFragment :
    Fragment() {

    interface OpdsEntryDetailsFragmentLinkClickListener {
        fun onClickOpdsEntryLink(link: Link)
    }


    private var listener: OpdsEntryDetailsFragmentLinkClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val startUrl: String? = requireArguments().getString(START_URL, null)
        if ( startUrl == null)
            requireActivity().supportFragmentManager.popBackStack()

        val opdsEntry: Entry? = requireArguments().getSerializable(ENTRY) as Entry?
        if ( opdsEntry == null)
            requireActivity().supportFragmentManager.popBackStack()

        return ComposeView(requireContext()).apply {
            setContent {
                OpdsEntryDetailsScreen(
                    drawerState = DrawerState(DrawerValue.Closed),
                    entry = opdsEntry!!,
                    startUrl = startUrl!!,
                    navigateBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    },
                    openInBrowser = {
                        openInBrowser(it, startUrl)
                    },
                    download = { e, l ->
                        download(e, l, startUrl)
                    },
                    onClickOpdsEntryLink = {
                        listener?.onClickOpdsEntryLink(it)
                    }
                )
            }
        }
    }


    private fun openInBrowser(link: Link, startUrl: String) {
        link.href?.let {
            Log.d("OPENLINK", "open link in browser $link")
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(UrlHelper.getUrl(it, startUrl))
            )
            startActivity(browserIntent)
        }
    }

    private fun download(
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

    fun setListener(listener: OpdsEntryDetailsFragmentLinkClickListener) {
        this.listener = listener
    }

    companion object {
        private const val ENTRY = "entry"
        private const val START_URL = "starturl"

        fun newInstance(entry: Entry, startUrl: String): OpdsEntryDetailsFragment {
            val frag = OpdsEntryDetailsFragment()
            val args = Bundle()
            args.putString(START_URL, startUrl)
            args.putSerializable(ENTRY, entry)
            frag.arguments = args
            return frag
        }
    }
}