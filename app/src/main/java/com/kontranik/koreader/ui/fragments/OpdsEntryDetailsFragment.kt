package com.kontranik.koreader.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kontranik.koreader.KoReaderApplication
import com.kontranik.koreader.databinding.FragmentOpdsEntrysDetailsBinding
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.OpdsTypes
import com.kontranik.koreader.ui.components.OpdsLinkOnClickListener
import com.kontranik.koreader.ui.components.OpdsLinkSpan
import com.kontranik.koreader.utils.ImageUtils
import com.kontranik.koreader.utils.UrlHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private lateinit var binding: FragmentOpdsEntrysDetailsBinding

    private var opdsEntry: Entry? = null

    private var startUrl: String? = null

    private var listener: OpdsEntryDetailsFragmentLinkClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpdsEntrysDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startUrl = requireArguments().getString(START_URL, null)
        if ( startUrl == null)
            requireActivity().supportFragmentManager.popBackStack()

        opdsEntry = requireArguments().getSerializable(ENTRY) as Entry?
        if ( opdsEntry == null)
            requireActivity().supportFragmentManager.popBackStack()

        binding.imageButtonOpdsentrydetailsBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        opdsEntry?.let {entry ->
            binding.textViewOpdsentrydetailsTitle.text = entry.title
            if (entry.author != null) {
                binding.textViewOpdsentrydetailsAuthor.visibility = View.VISIBLE
                binding.textViewOpdsentrydetailsAuthor.text = entry.author.toString()
            } else {
                binding.textViewOpdsentrydetailsAuthor.visibility = View.GONE
            }

            val content = SpannableStringBuilder(getHtml(entry.content?.data ?: ""))

            if (entry.otherLinks?.isNotEmpty() == true) content.append(getHtml("<h1>Links</h1>"))

            entry.otherLinks
                ?.sortedBy { link: Link -> link.rel }
                ?.groupBy { it.rel }
                ?.forEach { (rel, links) ->
                    content.append(getHtml("<h2>${OpdsTypes.mapRel(rel)}</h2>"))
                    links.forEach { link ->
                        content.append(OpdsLinkSpan(link, object : OpdsLinkOnClickListener {
                            override fun onClick(link: Link) {
                                Log.d("ENTRYLINK", "clicked entry.otherLinks: $link")
                                if (link.isCatalogEntry()) {
                                    listener?.onClickOpdsEntryLink(link)
                                    requireActivity().supportFragmentManager.popBackStack()
                                } else if (link.isDownloadable())  {
                                    download(entry, link)
                                } else  {
                                    openInBrowser(link)
                                }
                            }
                        }))
                        content.append(System.lineSeparator())
                    }
                    content.append(System.lineSeparator())
                }

            binding.textViewOpdsentrydetailsContent.text = content
            binding.textViewOpdsentrydetailsContent.movementMethod = LinkMovementMethod.getInstance()

            loadIcon(entry)
        }
    }

    private fun loadIcon(
        entry: Entry
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            var icon: Bitmap? = null
            try {
                if (entry.image?.href != null) {
                    icon = ImageUtils.drawableFromUrl(entry.image.href, startUrl)
                }
                withContext(Dispatchers.Main) {
                    if (icon != null) {
                        binding.imageViewOpdsentrydetailsCover.visibility = View.VISIBLE
                        binding.imageViewOpdsentrydetailsCover.setImageBitmap(icon)
                    } else {
                        binding.imageViewOpdsentrydetailsCover.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openInBrowser(link: Link) {
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
        link: Link
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

    private fun getHtml(html: String): Spanned {
        return HtmlCompat.fromHtml(
            html, HtmlCompat.FROM_HTML_MODE_COMPACT)
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